package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.DoneInternshipProcessDao;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.ExportRequest;
import com.teamaloha.internshipprocessmanagement.dto.InternshipProcess.InternshipProcessGetResponse;
import com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess.DoneInternshipProcessGetAllResponse;
import com.teamaloha.internshipprocessmanagement.dto.doneInternshipProcess.DoneInternshipProcessGetResponse;
import com.teamaloha.internshipprocessmanagement.entity.Company;
import com.teamaloha.internshipprocessmanagement.entity.DoneInternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.InternshipProcess;
import com.teamaloha.internshipprocessmanagement.entity.Student;
import com.teamaloha.internshipprocessmanagement.enums.ProcessStatusEnum;
import com.teamaloha.internshipprocessmanagement.exceptions.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class DoneInternshipProcessService {

    private final DoneInternshipProcessDao doneInternshipProcessDao;
    private final CompanyService companyService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DoneInternshipProcessService(DoneInternshipProcessDao doneInternshipProcessDao, CompanyService companyService) {
        this.doneInternshipProcessDao = doneInternshipProcessDao;
        this.companyService = companyService;
    }

    public DoneInternshipProcessGetAllResponse getAllDoneInternshipProcess(Integer studentId) {
        Student student = new Student();
        student.setId(studentId);

        List<DoneInternshipProcess> internshipProcessList = doneInternshipProcessDao.findAllByStudent(student);
        return createDoneInternshipProcessGetAllResponse(internshipProcessList);
    }

    public DoneInternshipProcessGetResponse getDoneInternshipProcess(Integer internshipProcessID, Integer studentId) {
        // Check if the process exists
        DoneInternshipProcess internshipProcess = getDoneInternshipProcessIfExistsOrThrowException(internshipProcessID);

        // Check if the current user id and the student id of the given internship process is matching.
        checkIfStudentIdAndDoneInternshipProcessMatchesOrThrowException(studentId, internshipProcess.getStudent().getId());

        DoneInternshipProcessGetResponse internshipProcessGetResponse = new DoneInternshipProcessGetResponse();

        copyEntityToDto(internshipProcess, internshipProcessGetResponse);

        return internshipProcessGetResponse;
    }

    public void exportExcel(ExportRequest exportRequest) {
        List<DoneInternshipProcess> doneInternshipProcesses = doneInternshipProcessDao.findAllByEndDateBetween(
                exportRequest.getStartDate(), exportRequest.getEndDate());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DoneInternshipProcesses");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Student Name");
        headerRow.createCell(1).setCellValue("Student Number");
        headerRow.createCell(2).setCellValue("TC Number");
        headerRow.createCell(3).setCellValue("Telephone Number");
        headerRow.createCell(4).setCellValue("Class");
        headerRow.createCell(5).setCellValue("Position");
        headerRow.createCell(6).setCellValue("Internship Type");
        headerRow.createCell(7).setCellValue("Internship Number");
        headerRow.createCell(8).setCellValue("Start Date");
        headerRow.createCell(9).setCellValue("End Date");
        headerRow.createCell(10).setCellValue("Company Name");
        headerRow.createCell(11).setCellValue("Department");
        headerRow.createCell(12).setCellValue("Engineer Name");
        headerRow.createCell(13).setCellValue("Engineer Email");


        // Populate data rows
        int rowNum = 1;
        for (DoneInternshipProcess process : doneInternshipProcesses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(process.getStudent().getFirstName() + " " +
                    process.getStudent().getLastName());
            row.createCell(1).setCellValue(process.getStudentNumber());
            row.createCell(2).setCellValue(process.getTc());
            row.createCell(3).setCellValue(process.getTelephoneNumber());
            row.createCell(4).setCellValue(process.getClassNumber());
            row.createCell(5).setCellValue(process.getPosition());
            row.createCell(6).setCellValue(process.getInternshipType());
            row.createCell(7).setCellValue(process.getInternshipNumber());
            row.createCell(8).setCellValue(process.getStartDate().toString());
            row.createCell(9).setCellValue(process.getEndDate().toString());
            row.createCell(10).setCellValue(process.getCompany().getCompanyName());
            row.createCell(11).setCellValue(process.getDepartment().getDepartmentName());
            row.createCell(12).setCellValue(process.getEngineerName());
            row.createCell(13).setCellValue(process.getEngineerMail());
        }

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(exportRequest.getFilePath())) {
            workbook.write(fileOut);
            fileOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close the workbook to release resources
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportPdf(ExportRequest exportRequest) {
        List<DoneInternshipProcess> doneInternshipProcesses = findAllByEndDateBetween(
                exportRequest.getStartDate(), exportRequest.getEndDate());

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();

                // Set position for header
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Student Name");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Student Number");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("TC Number");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Telephone Number");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Class");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Position");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Internship Type");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Internship Number");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Start Date");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("End Date");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Company Name");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Department");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Engineer Name");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Engineer Email");

                // Set position for data
                int yOffset = 680; // Starting y-coordinate for data
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (DoneInternshipProcess process : doneInternshipProcesses) {
                    contentStream.newLineAtOffset(-120, -20); // Reset x-coordinate for each row
                    contentStream.newLineAtOffset(50, yOffset);

                    // Add data to PDF
                    contentStream.showText(process.getStudent().getFirstName() + " " + process.getStudent().getLastName());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getStudentNumber());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getTc());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getTelephoneNumber());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(String.valueOf(process.getClassNumber()));
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getPosition());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getInternshipType());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(String.valueOf(process.getInternshipNumber()));
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(dateFormat.format(process.getStartDate()));
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(dateFormat.format(process.getEndDate()));
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getCompany().getCompanyName());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getDepartment().getDepartmentName());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getEngineerName());
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText(process.getEngineerMail());

                    yOffset -= 20; // Adjust the y-coordinate for the next row
                }

                contentStream.endText();
            }

            document.save(exportRequest.getFilePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    Set<Company> findCompainesByDateRange(Date startDate, Date endDate) {
        Set<DoneInternshipProcess> doneInternshipProcesses = doneInternshipProcessDao.findDoneInternshipProcessByStartDateAndEndDate(startDate, endDate);
        Set<Company> companies = new HashSet<>();

        for (DoneInternshipProcess doneInternshipProcess : doneInternshipProcesses) {

            Company company = companyService.findCompanyById(doneInternshipProcess.getCompany().getId());

            if (company != null)
                companies.add(companyService.findCompanyById(doneInternshipProcess.getCompany().getId()));
            else
                logger.error("Given company does not exists before. Company: " + doneInternshipProcess.getCompany().getCompanyName());

        }
        return companies;
    }


    public List<DoneInternshipProcess> findAllByEndDateBetween(Date startDate, Date endDate) {
        return doneInternshipProcessDao.findAllByEndDateBetween(startDate, endDate);
    }

    void save(DoneInternshipProcess doneInternshipProcess) {
        doneInternshipProcessDao.save(doneInternshipProcess);
    }

    private DoneInternshipProcessGetAllResponse createDoneInternshipProcessGetAllResponse(List<DoneInternshipProcess> internshipProcessList) {
        List<DoneInternshipProcessGetResponse> doneInternshipProcessGetResponseList = new ArrayList<>();
        for (DoneInternshipProcess internshipProcess : internshipProcessList) {
            DoneInternshipProcessGetResponse doneInternshipProcessGetResponse = new DoneInternshipProcessGetResponse();
            copyEntityToDto(internshipProcess, doneInternshipProcessGetResponse);
            doneInternshipProcessGetResponseList.add(doneInternshipProcessGetResponse);
        }
        return new DoneInternshipProcessGetAllResponse(doneInternshipProcessGetResponseList);
    }

    void copyEntityToDto(DoneInternshipProcess doneInternshipProcess, DoneInternshipProcessGetResponse doneInternshipProcessGetResponse) {
        BeanUtils.copyProperties(doneInternshipProcess, doneInternshipProcessGetResponse);

        if (doneInternshipProcess.getCompany() != null) {
            doneInternshipProcessGetResponse.setCompanyId(doneInternshipProcess.getCompany().getId());
            doneInternshipProcessGetResponse.setCompanyName(doneInternshipProcess.getCompany().getCompanyName());
        }

        if (doneInternshipProcess.getDepartment() != null) {
            doneInternshipProcessGetResponse.setDepartmentId(doneInternshipProcess.getDepartment().getId());
            doneInternshipProcessGetResponse.setDepartmentName(doneInternshipProcess.getDepartment().getDepartmentName());
        }

        if (doneInternshipProcess.getStartDate() != null) {
            doneInternshipProcessGetResponse.setStartDateStr(
                    UtilityService.convertDate(doneInternshipProcess.getStartDate(), UtilityService.format1));
        }

        if (doneInternshipProcess.getEndDate() != null) {
            doneInternshipProcessGetResponse.setEndDateStr(
                    UtilityService.convertDate(doneInternshipProcess.getEndDate(), UtilityService.format1));
        }

        doneInternshipProcessGetResponse.setStudentId(doneInternshipProcess.getStudent().getId());
        doneInternshipProcessGetResponse.setFullName(doneInternshipProcess.getStudent().getFirstName() + " " + doneInternshipProcess.getStudent().getLastName());
        doneInternshipProcessGetResponse.setUpdateDate(doneInternshipProcess.getLogDates().getUpdateDate());
    }

    private DoneInternshipProcess getDoneInternshipProcessIfExistsOrThrowException(Integer processId) {
        DoneInternshipProcess internshipProcess = doneInternshipProcessDao.findDoneInternshipProcessById(processId);
        if (internshipProcess == null) {
            logger.error("DoneInternshipProcess with ID " + processId + " not found for update.");
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
        return internshipProcess;
    }

    private void checkIfStudentIdAndDoneInternshipProcessMatchesOrThrowException(Integer userId, Integer processId) {
        if (!userId.equals(processId)) {
            logger.error("The internshipProcess id given does not belong to the student. Student id: "
                    + userId);
            throw new CustomException(HttpStatus.BAD_REQUEST);
        }
    }

    public Integer countDoneInternshipProcessByStudentIdAndProcessStatus(Integer studentId, ProcessStatusEnum processStatus) {
        return doneInternshipProcessDao.countByStudentIdAndProcessStatus(studentId, processStatus);
    }
}

