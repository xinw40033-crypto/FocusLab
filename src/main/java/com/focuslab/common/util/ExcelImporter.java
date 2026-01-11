package com.focuslab.common.util;

import com.focuslab.common.entity.TrainingContent;
import com.focuslab.common.entity.TrainingType;
import com.focuslab.common.repository.TrainingContentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Excel数据导入工具
 * 用于从Excel文件导入训练内容
 */
@Component
public class ExcelImporter {

    @Autowired
    private TrainingContentRepository trainingContentRepository;

    public void importFromExcel(String excelFilePath, String imageDir) throws IOException {
        FileInputStream fis = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // 跳过标题行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                TrainingContent content = parseRow(row, imageDir);
                if (content != null) {
                    trainingContentRepository.save(content);
                    System.out.println("导入成功: " + content.getTitle());
                }
            } catch (Exception e) {
                System.err.println("导入第 " + (i + 1) + " 行失败: " + e.getMessage());
            }
        }

        workbook.close();
        fis.close();
    }

    private TrainingContent parseRow(Row row, String imageDir) {
        TrainingContent content = new TrainingContent();

        // 读取基本信息
        String type = getCellValue(row.getCell(0));
        String title = getCellValue(row.getCell(1));
        String imageName = getCellValue(row.getCell(2));
        String difficultyStr = getCellValue(row.getCell(3));

        if (type == null || title == null || imageName == null) {
            return null;
        }

        content.setType(TrainingType.valueOf(type));
        content.setTitle(title);
        content.setImagePath("/images/" + imageName);
        content.setDifficulty(Integer.parseInt(difficultyStr));

        // 根据训练类型构建config
        String config = buildConfig(content.getType(), row);
        content.setConfig(config);

        return content;
    }

    private String buildConfig(TrainingType type, Row row) {
        switch (type) {
            case ARROW_TRACKING:
                return buildArrowTrackingConfig(row);
            case NUMBER_REPETITION:
                return buildNumberRepetitionConfig(row);
            case ANIMAL_NUMBER_MATCH:
                return buildAnimalNumberMatchConfig(row);
            case HIDDEN_OBJECT:
                return buildHiddenObjectConfig(row);
            case NUMBER_COMPARE:
                return buildNumberCompareConfig(row);
            case SCHULTZ_TABLE:
                return buildSchultzTableConfig(row);
            case SYMBOL_MATRIX:
                return buildSymbolMatrixConfig(row);
            case NUMBER_MIGRATION:
                return buildNumberMigrationConfig(row);
            default:
                return "{}";
        }
    }

    // 箭头追踪配置
    private String buildArrowTrackingConfig(Row row) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"path\":[");
        
        // 从第4列开始读取坐标点（每两列一个坐标）
        int colIndex = 4;
        boolean hasMore = true;
        while (hasMore && colIndex < row.getLastCellNum()) {
            String xStr = getCellValue(row.getCell(colIndex));
            String yStr = getCellValue(row.getCell(colIndex + 1));
            
            if (xStr != null && yStr != null && !xStr.isEmpty() && !yStr.isEmpty()) {
                if (colIndex > 4) sb.append(",");
                sb.append("[").append(xStr).append(",").append(yStr).append("]");
                colIndex += 2;
            } else {
                hasMore = false;
            }
        }
        
        sb.append("]}");
        return sb.toString();
    }

    // 数字重复查找配置
    private String buildNumberRepetitionConfig(Row row) {
        return "{\"rows\":[[{\"val\":10,\"pos\":[2,15]},{\"val\":4,\"pos\":[3]}]]}";
    }

    // 动物数字匹配配置
    private String buildAnimalNumberMatchConfig(Row row) {
        return "[{\"animal\":\"pig\",\"pos\":[200,300],\"target\":21}]";
    }

    // 寻宝游戏配置
    private String buildHiddenObjectConfig(Row row) {
        return "{\"objects\":[{\"name\":\"ball\",\"x\":150,\"y\":200}]}";
    }

    // 数字对比配置
    private String buildNumberCompareConfig(Row row) {
        return "{\"pairs\":[{\"num1\":45,\"num2\":32,\"isGreater\":true}]}";
    }

    // 舒尔特方格配置
    private String buildSchultzTableConfig(Row row) {
        return "{\"sequence\":[1,2,3,4,5,6,7,8,9]}";
    }

    // 符号矩阵配置
    private String buildSymbolMatrixConfig(Row row) {
        return "{\"targets\":[{\"position\":\"A1\",\"value\":\"X\"}]}";
    }

    // 数字迁移配置
    private String buildNumberMigrationConfig(Row row) {
        return "{\"migrations\":[{\"start\":\"A1\",\"end\":\"B2\",\"number\":5}]}";
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
