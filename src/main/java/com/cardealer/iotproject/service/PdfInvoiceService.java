package com.cardealer.iotproject.service;

import com.cardealer.iotproject.config.AppConfig;
import com.cardealer.iotproject.model.entity.*;
import com.cardealer.iotproject.repository.CompanyRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfInvoiceService {
    
    private final InvoiceService invoiceService;
    private final CompanyRepository companyRepository;
    private final AppConfig appConfig;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    private static final BigDecimal ITBIS_RATE = new BigDecimal("18.00");
    
    public PdfInvoiceService(InvoiceService invoiceService, 
                             CompanyRepository companyRepository,
                             AppConfig appConfig) {
        this.invoiceService = invoiceService;
        this.companyRepository = companyRepository;
        this.appConfig = appConfig;
    }
    
    public byte[] generateInvoicePdf(Long invoiceId) throws DocumentException, IOException {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        List<InvoiceItem> items = invoice.getItems();
        Company company = getCompanyInfo();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        
        // Add metadata
        document.addTitle("Factura Electrónica " + invoice.getEnNcf());
        document.addAuthor(company != null ? company.getCompanyName() : "Car Dealer IoT");
        document.addSubject("Factura de Venta");
        
        // Add content
        addHeader(document, company, invoice);
        addCustomerInfo(document, invoice);
        addInvoiceDetails(document, invoice);
        addItemsTable(document, items, invoice);
        addTotals(document, invoice);
        addFooter(document, company);
        
        document.close();
        return baos.toByteArray();
    }
    
    private Company getCompanyInfo() {
        List<Company> companies = companyRepository.findAll();
        return companies.isEmpty() ? null : companies.get(0);
    }
    
    private void addHeader(com.itextpdf.text.Document document, Company company, Invoice invoice) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});
        
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        
        
        // Load and add the logo
        Image logo = loadLogoFromCompany(company);
        if (logo != null) {
            try {
                logo.scaleToFit(80, 80);
                leftCell.addElement(logo);
                leftCell.addElement(new Paragraph(" "));
            } catch (Exception e) {
                System.err.println("Error scaling logo: " + e.getMessage());
                addTextLogo(leftCell, company);
            }
        } else {
            addTextLogo(leftCell, company);
        }
        
        // Add company details
        if (company != null) {
            Paragraph companyName = new Paragraph(company.getCompanyName(), 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            leftCell.addElement(companyName);
            
            if (company.getRnc() != null && !company.getRnc().isEmpty()) {
                leftCell.addElement(new Paragraph("RNC: " + company.getRnc(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9)));
            }
            if (company.getPhone() != null && !company.getPhone().isEmpty()) {
                leftCell.addElement(new Paragraph("Tel: " + company.getPhone(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9)));
            }
            if (company.getAddress() != null && !company.getAddress().isEmpty()) {
                leftCell.addElement(new Paragraph(company.getAddress(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9)));
            }
        } else {
            leftCell.addElement(new Paragraph("CAR DEALER IoT", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        }
        
        headerTable.addCell(leftCell);
        
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph title = new Paragraph("FACTURA ELECTRÓNICA", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.RED));
        rightCell.addElement(title);
        rightCell.addElement(new Paragraph(" "));
        
        Paragraph ncf = new Paragraph("NCF: " + invoice.getEnNcf(), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        rightCell.addElement(ncf);
        
        headerTable.addCell(rightCell);
        
        document.add(headerTable);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _"));
        document.add(new Paragraph(" "));
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
        
        // Method 2: Try as URL with server (using AppConfig)
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
        Paragraph logoText = new Paragraph("🏢", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28));
        cell.addElement(logoText);
    }
    
    private void addCustomerInfo(com.itextpdf.text.Document document, Invoice invoice) throws DocumentException {
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(5);
        Paragraph sectionTitle = new Paragraph("DATOS DEL CLIENTE", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        titleCell.addElement(sectionTitle);
        
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.addCell(titleCell);
        document.add(titleTable);
        
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 2});
        
        infoTable.addCell(createLabelCell("Nombre:"));
        infoTable.addCell(createValueCell(invoice.getCustomerName()));
        
        if (invoice.getCustomerRnc() != null && !invoice.getCustomerRnc().isEmpty()) {
            infoTable.addCell(createLabelCell("RNC:"));
            infoTable.addCell(createValueCell(invoice.getCustomerRnc()));
        }
        
        if (invoice.getCustomerCedula() != null && !invoice.getCustomerCedula().isEmpty()) {
            infoTable.addCell(createLabelCell("Cédula:"));
            infoTable.addCell(createValueCell(invoice.getCustomerCedula()));
        }
        
        if (invoice.getCustomerPhone() != null && !invoice.getCustomerPhone().isEmpty()) {
            infoTable.addCell(createLabelCell("Teléfono:"));
            infoTable.addCell(createValueCell(invoice.getCustomerPhone()));
        }
        
        if (invoice.getCustomerEmail() != null && !invoice.getCustomerEmail().isEmpty()) {
            infoTable.addCell(createLabelCell("Email:"));
            infoTable.addCell(createValueCell(invoice.getCustomerEmail()));
        }
        
        if (invoice.getCustomerAddress() != null && !invoice.getCustomerAddress().isEmpty()) {
            infoTable.addCell(createLabelCell("Dirección:"));
            infoTable.addCell(createValueCell(invoice.getCustomerAddress()));
        }
        
        document.add(infoTable);
        document.add(new Paragraph(" "));
    }
    
    private void addInvoiceDetails(com.itextpdf.text.Document document, Invoice invoice) throws DocumentException {
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(5);
        Paragraph sectionTitle = new Paragraph("INFORMACIÓN DE LA FACTURA", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        titleCell.addElement(sectionTitle);
        
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.addCell(titleCell);
        document.add(titleTable);
        
        PdfPTable detailsTable = new PdfPTable(4);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{1, 2, 1, 2});
        
        detailsTable.addCell(createLabelCell("Número:"));
        detailsTable.addCell(createValueCell(invoice.getEnNcf()));
        detailsTable.addCell(createLabelCell("Fecha:"));
        detailsTable.addCell(createValueCell(invoice.getInvoiceDateTime()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        
        detailsTable.addCell(createLabelCell("Tipo:"));
        detailsTable.addCell(createValueCell(invoice.getNcfType().equals("31") ? 
            "Crédito Fiscal" : "Consumo"));
        detailsTable.addCell(createLabelCell("Estado:"));
        detailsTable.addCell(createValueCell(invoice.getStatus()));
        
        document.add(detailsTable);
        document.add(new Paragraph(" "));
    }
    
    private void addItemsTable(com.itextpdf.text.Document document, List<InvoiceItem> items, Invoice invoice) throws DocumentException {
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPadding(5);
        Paragraph sectionTitle = new Paragraph("DETALLE DE LA TRANSACCIÓN", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        titleCell.addElement(sectionTitle);
        
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.addCell(titleCell);
        document.add(titleTable);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 2, 1, 2});
        table.setHeaderRows(1);
        
        // Table header
        addTableHeaderCell(table, "Cant.");
        addTableHeaderCell(table, "Descripción");
        addTableHeaderCell(table, "Precio Unit.");
        addTableHeaderCell(table, "ITBIS");
        addTableHeaderCell(table, "Total");
        
        // Table body
        for (InvoiceItem item : items) {
            table.addCell(createValueCell(String.valueOf(item.getQuantity())));
            table.addCell(createValueCell(item.getDescription()));
            table.addCell(createRightAlignCell(formatCurrency(item.getUnitPrice())));
            table.addCell(createRightAlignCell("18%"));
            table.addCell(createRightAlignCell(formatCurrency(item.getTotal())));
        }
        
        document.add(table);
        document.add(new Paragraph(" "));
    }
    
    private void addTotals(com.itextpdf.text.Document document, Invoice invoice) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{1, 1});
        
        totalsTable.addCell(createRightAlignLabelCell("Subtotal:"));
        totalsTable.addCell(createRightAlignValueCell(formatCurrency(invoice.getSubtotal())));
        
        totalsTable.addCell(createRightAlignLabelCell("ITBIS (18%):"));
        totalsTable.addCell(createRightAlignValueCell(formatCurrency(invoice.getItbisAmount())));
        
        totalsTable.addCell(createRightAlignLabelCell("TOTAL:"));
        PdfPCell totalCell = createRightAlignValueCell(formatCurrency(invoice.getTotal()));
        totalCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        totalCell.setPhrase(new Phrase(formatCurrency(invoice.getTotal()), boldFont));
        totalsTable.addCell(totalCell);
        
        document.add(totalsTable);
        document.add(new Paragraph(" "));
    }
    
    private void addFooter(com.itextpdf.text.Document document, Company company) throws DocumentException {
        document.add(new Paragraph("_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _"));
        document.add(new Paragraph(" "));
        
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        
        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.NO_BORDER);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        footerCell.addElement(new Paragraph("¡Gracias por su compra!", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        footerCell.addElement(new Paragraph(" "));
        footerCell.addElement(new Paragraph("Este documento es una representación impresa de una factura electrónica.", 
            FontFactory.getFont(FontFactory.HELVETICA, 8)));
        footerCell.addElement(new Paragraph("Verifique la validez del NCF en el portal de la DGII: https://dgii.gov.do", 
            FontFactory.getFont(FontFactory.HELVETICA, 7)));
        
        if (company != null && company.getEmailAddr() != null && !company.getEmailAddr().isEmpty()) {
            footerCell.addElement(new Paragraph(company.getEmailAddr(), 
                FontFactory.getFont(FontFactory.HELVETICA, 7)));
        }
        
        footerTable.addCell(footerCell);
        document.add(footerTable);
    }
    
    // Helper methods
    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }
    
    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null && !text.isEmpty() ? text : "N/A", 
            FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        return cell;
    }
    
    private PdfPCell createRightAlignCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
    
    private PdfPCell createRightAlignLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
    
    private PdfPCell createRightAlignValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
    
    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
    
    private String formatCurrency(BigDecimal value) {
        if (value == null) return "RD$ 0.00";
        return String.format("RD$ %,.2f", value);
    }
}