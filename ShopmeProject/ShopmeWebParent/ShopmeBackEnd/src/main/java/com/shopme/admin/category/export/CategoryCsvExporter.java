package com.shopme.admin.category.export;

import com.shopme.admin.AbstractExporter;
import com.shopme.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {

    public void export(List<Category> categories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Name"};
        String[] fieldMapping = {"id", "name"};

        csvWriter.writeHeader(csvHeader);

        for (Category category : categories) {
            category.setName(category.getName().replace("--", "  "));
            csvWriter.write(category, fieldMapping);
        }

        csvWriter.close();
    }
}
