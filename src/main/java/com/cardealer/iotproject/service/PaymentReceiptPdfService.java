package com.cardealer.iotproject.service;

import com.cardealer.iotproject.config.AppConfig;
import com.cardealer.iotproject.model.entity.Company;
import com.cardealer.iotproject.model.entity.Invoice;
import com.cardealer.iotproject.model.entity.Payment;
import com.cardealer.iotproject.model.entity.Vehicle;
import com.cardealer.iotproject.repository.CompanyRepository;
import com.cardealer.iotproject.repository.PaymentRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentReceiptPdfService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private AppConfig appConfig;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    
    public byte[] generatePaymentReceipt(Long paymentId) throws Exception {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + paymentId));
        
        Invoice invoice = payment.getInvoice();
        Vehicle vehicle = invoice.getVehicle();
        
        Company company = getCompanyInfo();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();
        
        // Add metadata
        document.addTitle("Recibo de Pago " + payment.getReceiptNumber());
        document.addAuthor(company != null ? company.getCompanyName() : "Car Dealer IoT");
        document.addSubject("Recibo de Pago");
        
        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        Font verySmallFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
        
        // Header con Logo a la izquierda y texto a la derecha
        addHeader(document, company);
        
        // Divider
        Paragraph divider = new Paragraph("-------------------------------------------------------------------", verySmallFont);
        divider.setAlignment(Element.ALIGN_CENTER);
        document.add(divider);
        
        // Receipt Title
        Paragraph receiptTitle = new Paragraph("RECIBO DE PAGO", headerFont);
        receiptTitle.setAlignment(Element.ALIGN_CENTER);
        receiptTitle.setSpacingBefore(10);
        document.add(receiptTitle);
        
        Paragraph receiptNumber = new Paragraph("Recibo No.: " + payment.getReceiptNumber(), boldFont);
        receiptNumber.setAlignment(Element.ALIGN_CENTER);
        document.add(receiptNumber);
        
        document.add(new Paragraph(" "));
        
        // Payment Info Table
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{30, 70});
        
        addInfoRow(infoTable, "Fecha de Pago:", payment.getPaymentDate().format(DATE_FORMATTER), boldFont, normalFont);
        addInfoRow(infoTable, "Método de Pago:", getPaymentMethodSpanish(payment.getPaymentMethod()), boldFont, normalFont);
        
        if (payment.getReferenceNumber() != null && !payment.getReferenceNumber().isEmpty()) {
            addInfoRow(infoTable, "Referencia:", payment.getReferenceNumber(), boldFont, normalFont);
        }
        
        addInfoRow(infoTable, "Tipo de Pago:", getPaymentTypeSpanish(payment.getPaymentType()), boldFont, normalFont);
        
        if ("OTHER".equals(payment.getPaymentType()) && payment.getOtherReason() != null && !payment.getOtherReason().isEmpty()) {
            addInfoRow(infoTable, "Razón:", payment.getOtherReason(), boldFont, normalFont);
        }
        
        document.add(infoTable);
        
        document.add(new Paragraph(" "));
        
        // VEHICLE INFORMATION SECTION
        if (vehicle != null && "VEHICLE_PURCHASE".equals(payment.getPaymentType())) {
            PdfPTable vehicleHeaderTable = new PdfPTable(1);
            vehicleHeaderTable.setWidthPercentage(100);
            PdfPCell vehicleHeaderCell = new PdfPCell(new Phrase("INFORMACIÓN DEL VEHÍCULO", headerFont));
            vehicleHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            vehicleHeaderCell.setPadding(5);
            vehicleHeaderCell.setBorder(Rectangle.NO_BORDER);
            vehicleHeaderTable.addCell(vehicleHeaderCell);
            document.add(vehicleHeaderTable);
            
            PdfPTable vehicleTable = new PdfPTable(2);
            vehicleTable.setWidthPercentage(100);
            vehicleTable.setWidths(new float[]{30, 70});
            
            // Marca
            if (vehicle.getMake() != null && vehicle.getMake().getMakeName() != null) {
                addInfoRow(vehicleTable, "Marca:", vehicle.getMake().getMakeName(), boldFont, normalFont);
            }
            
            // Modelo
            if (vehicle.getModel() != null && vehicle.getModel().getModelName() != null) {
                addInfoRow(vehicleTable, "Modelo:", vehicle.getModel().getModelName(), boldFont, normalFont);
            }
            
            // Año
            if (vehicle.getModelYear() != null && vehicle.getModelYear() > 0) {
                addInfoRow(vehicleTable, "Año:", String.valueOf(vehicle.getModelYear()), boldFont, normalFont);
            }
            
            // VIN
            if (vehicle.getVin() != null && !vehicle.getVin().isEmpty()) {
                addInfoRow(vehicleTable, "VIN:", vehicle.getVin(), boldFont, normalFont);
            }
            
            // Color
            if (vehicle.getColor() != null && !vehicle.getColor().isEmpty()) {
                addInfoRow(vehicleTable, "Color:", vehicle.getColor(), boldFont, normalFont);
            }
            
            // Kilometraje
            if (vehicle.getMileage() != null && vehicle.getMileage() > 0) {
                String mileageUnit = vehicle.getMileageUnit() != null ? vehicle.getMileageUnit() : "KM";
                addInfoRow(vehicleTable, "Kilometraje:", vehicle.getMileage() + " " + mileageUnit, boldFont, normalFont);
            }
            
            // Precio de Venta
            if (vehicle.getSellingPrice() != null) {
                addInfoRow(vehicleTable, "Precio de Venta:", formatCurrency(vehicle.getSellingPrice()), boldFont, normalFont);
            }
            
            // Condición
            if (vehicle.getCondition() != null) {
                String condition = getConditionSpanish(vehicle.getCondition().name());
                addInfoRow(vehicleTable, "Condición:", condition, boldFont, normalFont);
            }
            
            document.add(vehicleTable);
            document.add(new Paragraph(" "));
        }
        
        // Invoice Info Header
        PdfPTable invoiceHeaderTable = new PdfPTable(1);
        invoiceHeaderTable.setWidthPercentage(100);
        PdfPCell invoiceHeaderCell = new PdfPCell(new Phrase("DETALLE DE LA FACTURA", headerFont));
        invoiceHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        invoiceHeaderCell.setPadding(5);
        invoiceHeaderCell.setBorder(Rectangle.NO_BORDER);
        invoiceHeaderTable.addCell(invoiceHeaderCell);
        document.add(invoiceHeaderTable);
        
        // Invoice Info Table
        PdfPTable invoiceTable = new PdfPTable(2);
        invoiceTable.setWidthPercentage(100);
        invoiceTable.setWidths(new float[]{30, 70});
        
        addInfoRow(invoiceTable, "Factura No.:", invoice.getEnNcf(), boldFont, normalFont);
        addInfoRow(invoiceTable, "Cliente:", invoice.getCustomerName(), boldFont, normalFont);
        
        String customerId = (invoice.getCustomerRnc() != null && !invoice.getCustomerRnc().isEmpty()) 
            ? invoice.getCustomerRnc() 
            : (invoice.getCustomerCedula() != null && !invoice.getCustomerCedula().isEmpty() 
                ? invoice.getCustomerCedula() 
                : "N/A");
        addInfoRow(invoiceTable, "RNC/Cédula:", customerId, boldFont, normalFont);
        
        addInfoRow(invoiceTable, "Fecha Factura:", invoice.getInvoiceDateTime().format(DATE_FORMATTER), boldFont, normalFont);
        
        document.add(invoiceTable);
        
        document.add(new Paragraph(" "));
        
        // Payment Details Header
        PdfPTable paymentHeaderTable = new PdfPTable(1);
        paymentHeaderTable.setWidthPercentage(100);
        PdfPCell paymentHeaderCell = new PdfPCell(new Phrase("DETALLE DEL PAGO", headerFont));
        paymentHeaderCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        paymentHeaderCell.setPadding(5);
        paymentHeaderCell.setBorder(Rectangle.NO_BORDER);
        paymentHeaderTable.addCell(paymentHeaderCell);
        document.add(paymentHeaderTable);
        
        // Payment Details Table
        PdfPTable paymentTable = new PdfPTable(2);
        paymentTable.setWidthPercentage(100);
        paymentTable.setWidths(new float[]{50, 50});
        
        // Calculate total paid and balance
        BigDecimal totalPaid = paymentRepository.getTotalPaidByInvoiceId(invoice.getId());
        if (totalPaid == null) totalPaid = payment.getAmount();
        BigDecimal remainingBalance = invoice.getTotal().subtract(totalPaid);
        
        addAmountRow(paymentTable, "Total Factura:", invoice.getTotal(), boldFont);
        addAmountRow(paymentTable, "Total Pagado:", totalPaid, boldFont);
        addAmountRow(paymentTable, "Este Pago:", payment.getAmount(), boldFont);
        
        if (remainingBalance.compareTo(BigDecimal.ZERO) > 0) {
            addAmountRow(paymentTable, "Balance Pendiente:", remainingBalance, boldFont);
        }
        
        document.add(paymentTable);
        
        document.add(new Paragraph(" "));
        
        // Invoice Status
        String statusText = getInvoiceStatusSpanish(invoice.getStatus());
        Paragraph statusParagraph = new Paragraph("Estado de la Factura: " + statusText, boldFont);
        statusParagraph.setAlignment(Element.ALIGN_CENTER);
        
        if ("PAID".equals(invoice.getStatus())) {
            statusParagraph.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.GREEN));
        } else if ("PARTIALLY_PAID".equals(invoice.getStatus())) {
            statusParagraph.setFont(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.ORANGE));
        }
        
        document.add(statusParagraph);
        
        document.add(new Paragraph(" "));
        
        // Footer
        addFooter(document, company);
        
        // Notes if present
        if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
            Paragraph notesLabel = new Paragraph("Notas:", boldFont);
            notesLabel.setSpacingBefore(10);
            document.add(notesLabel);
            
            Paragraph notes = new Paragraph(payment.getNotes(), normalFont);
            document.add(notes);
        }
        
        document.close();
        return baos.toByteArray();
    }
    
    private void addHeader(Document document, Company company) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{25, 75});
        
        // Left Cell - Logo
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Image logo = loadLogoFromCompany(company);
        if (logo != null) {
            try {
                logo.scaleToFit(70, 70);
                logo.setAlignment(Element.ALIGN_CENTER);
                leftCell.addElement(logo);
            } catch (Exception e) {
                System.err.println("Error scaling logo: " + e.getMessage());
                addTextLogo(leftCell, company);
            }
        } else {
            addTextLogo(leftCell, company);
        }
        
        headerTable.addCell(leftCell);
        
        // Right Cell - Company Info (con fuentes más pequeñas)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        // Company Name (tamaño normal)
        String companyName = company != null && company.getCompanyName() != null 
            ? company.getCompanyName().toUpperCase() 
            : "CAR DEALER - CONCESIONARIO";
        Paragraph companyNamePara = new Paragraph(companyName, 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        companyNamePara.setAlignment(Element.ALIGN_LEFT);
        rightCell.addElement(companyNamePara);
        
        // Company RNC (fuente muy pequeña)
        if (company != null && company.getRnc() != null && !company.getRnc().isEmpty()) {
            Paragraph companyRnc = new Paragraph("RNC: " + company.getRnc(), 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            companyRnc.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(companyRnc);
        }
        
        // Company Address (fuente muy pequeña)
        String address = "";
        if (company != null) {
            if (company.getAddress() != null) address += company.getAddress();
            if (company.getCity() != null) address += (address.isEmpty() ? "" : ", ") + company.getCity();
            if (company.getProvincia() != null) address += (address.isEmpty() ? "" : ", ") + company.getProvincia();
        }
        if (address.isEmpty()) {
            address = "Av. Principal #123, Santo Domingo, República Dominicana";
        }
        Paragraph companyAddress = new Paragraph(address, FontFactory.getFont(FontFactory.HELVETICA, 7));
        companyAddress.setAlignment(Element.ALIGN_LEFT);
        rightCell.addElement(companyAddress);
        
        // Company Phone (fuente muy pequeña)
        if (company != null && company.getPhone() != null && !company.getPhone().isEmpty()) {
            Paragraph companyPhone = new Paragraph("Tel: " + company.getPhone(), 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            companyPhone.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(companyPhone);
        }
        
        // Company Email (fuente muy pequeña)
        if (company != null && company.getEmailAddr() != null && !company.getEmailAddr().isEmpty()) {
            Paragraph companyEmail = new Paragraph("Email: " + company.getEmailAddr(), 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            companyEmail.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(companyEmail);
        }
        
        // Website (fuente muy pequeña)
        if (company != null && company.getWebsite() != null && !company.getWebsite().isEmpty()) {
            Paragraph website = new Paragraph(company.getWebsite(), 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            website.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(website);
        }
        
        // Si no hay información de empresa, mostrar valores por defecto
        if (company == null) {
            Paragraph defaultPhone = new Paragraph("Tel: (809) 555-1234", 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            defaultPhone.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(defaultPhone);
            
            Paragraph defaultEmail = new Paragraph("Email: info@cardealer.com", 
                FontFactory.getFont(FontFactory.HELVETICA, 7));
            defaultEmail.setAlignment(Element.ALIGN_LEFT);
            rightCell.addElement(defaultEmail);
        }
        
        headerTable.addCell(rightCell);
        document.add(headerTable);
    }
    
    private Image loadLogoFromCompany(Company company) {
        if (company == null || company.getLogoPath() == null || company.getLogoPath().isEmpty()) {
            System.out.println("No logo path found in company record");
            return null;
        }
        
        String logoPath = company.getLogoPath();
        System.out.println("Attempting to load logo from path: " + logoPath);
        
        // Method 1: Try from uploads directory
        try {
            String cleanPath = logoPath;
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }
            if (cleanPath.startsWith("api/")) {
                cleanPath = cleanPath.substring(4);
            }
            
            String[] possiblePaths = {
                cleanPath,
                "./" + cleanPath,
                System.getProperty("user.dir") + "/" + cleanPath,
                "uploads/company/" + new File(cleanPath).getName(),
                "./uploads/company/" + new File(cleanPath).getName()
            };
            
            for (String path : possiblePaths) {
                File logoFile = new File(path);
                if (logoFile.exists()) {
                    System.out.println("Found logo at: " + logoFile.getAbsolutePath());
                    Image logo = Image.getInstance(logoFile.getAbsolutePath());
                    return logo;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading from filesystem: " + e.getMessage());
        }
        
        // Method 2: Try as URL with server
        try {
            String fullUrl = appConfig.getApiBaseUrl() + contextPath + logoPath;
            System.out.println("Trying URL: " + fullUrl);
            Image logo = Image.getInstance(new URL(fullUrl));
            System.out.println("Successfully loaded logo from URL");
            return logo;
        } catch (Exception e) {
            System.err.println("Error loading from URL: " + e.getMessage());
        }
        
        // Method 3: Try scanning uploads directory
        try {
            File uploadsDir = new File("uploads/company");
            if (uploadsDir.exists() && uploadsDir.isDirectory()) {
                File[] files = uploadsDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".jpeg") || 
                    name.toLowerCase().endsWith(".png"));
                
                if (files != null && files.length > 0) {
                    File latestLogo = files[0];
                    for (File file : files) {
                        if (file.lastModified() > latestLogo.lastModified()) {
                            latestLogo = file;
                        }
                    }
                    System.out.println("Found logo in uploads directory: " + latestLogo.getName());
                    Image logo = Image.getInstance(latestLogo.getAbsolutePath());
                    return logo;
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning uploads directory: " + e.getMessage());
        }
        
        System.out.println("Could not load logo from any source");
        return null;
    }
    
    private void addTextLogo(PdfPCell cell, Company company) {
        Paragraph logoText = new Paragraph("🚗", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 40));
        logoText.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(logoText);
    }
    
    private void addFooter(Document document, Company company) throws DocumentException {
        Paragraph divider = new Paragraph("-------------------------------------------------------------------", 
            FontFactory.getFont(FontFactory.HELVETICA, 7));
        divider.setAlignment(Element.ALIGN_CENTER);
        document.add(divider);
        
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        
        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.NO_BORDER);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        Paragraph thankYou = new Paragraph("¡Gracias por su preferencia!", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
        thankYou.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(thankYou);
        
        footerCell.addElement(new Paragraph(" "));
        
        Paragraph footerNote = new Paragraph("Este documento es un comprobante de pago válido", 
            FontFactory.getFont(FontFactory.HELVETICA, 7));
        footerNote.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(footerNote);
        
        footerTable.addCell(footerCell);
        document.add(footerTable);
    }
    
    private Company getCompanyInfo() {
        try {
            List<Company> companies = companyRepository.findAll();
            return companies.isEmpty() ? null : companies.get(0);
        } catch (Exception e) {
            System.err.println("Error getting company info: " + e.getMessage());
            return null;
        }
    }
    
    private void addInfoRow(PdfPTable table, String label, String value, Font boldFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "", normalFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private void addAmountRow(PdfPTable table, String label, BigDecimal amount, Font boldFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        
        String amountStr = String.format("RD$ %,.2f", amount);
        PdfPCell valueCell = new PdfPCell(new Phrase(amountStr, boldFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private String formatCurrency(BigDecimal value) {
        if (value == null) return "RD$ 0.00";
        return String.format("RD$ %,.2f", value);
    }
    
    private String getConditionSpanish(String condition) {
        if (condition == null) return "N/A";
        switch (condition) {
            case "NEW": return "Nuevo";
            case "USED": return "Usado";
            case "CERTIFIED_PRE_OWNED": return "Certificado";
            default: return condition;
        }
    }
    
    private String getPaymentMethodSpanish(String method) {
        if (method == null) return "N/A";
        
        switch (method) {
            case "CASH":
                return "Efectivo";
            case "CREDIT_CARD":
                return "Tarjeta de Crédito/Débito";
            case "BANK_TRANSFER":
                return "Transferencia Bancaria";
            case "CHECK":
                return "Cheque";
            case "FINANCING":
                return "Financiamiento";
            default:
                return method;
        }
    }
    
    private String getPaymentTypeSpanish(String type) {
        if (type == null) return "N/A";
        
        switch (type) {
            case "VEHICLE_PURCHASE":
                return "Compra de Vehículo";
            case "SERVICE":
                return "Servicio";
            case "OTHER":
                return "Otro";
            default:
                return type;
        }
    }
    
    private String getInvoiceStatusSpanish(String status) {
        if (status == null) return "N/A";
        
        switch (status) {
            case "PAID":
                return "PAGADA";
            case "PARTIALLY_PAID":
                return "PAGO PARCIAL";
            case "PENDING":
                return "PENDIENTE";
            case "CANCELLED":
                return "CANCELADA";
            case "VOID":
                return "ANULADA";
            default:
                return status;
        }
    }
}