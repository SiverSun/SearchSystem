package org.example;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (var pdfFile : pdfsDir.listFiles()) {
            var doc = new PdfDocument(new PdfReader(pdfFile));
            for (int i = 1; i < doc.getNumberOfPages() + 1; i++) {
                Map<String, Integer> freqs = new HashMap<>();
                PdfPage page = doc.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    String word = entry.getKey();
                    Integer count = entry.getValue();
                    PageEntry pageEntry = new PageEntry(pdfFile.getName(), i, count);

                    List<PageEntry> pageEntries = map.getOrDefault(word, new ArrayList<>());
                    pageEntries.add(pageEntry);
                    map.put(word, pageEntries);
                }
            }
        }
        map.values()
                .forEach(Collections::sort);
    }

    @Override
    public List<PageEntry> search(String word) {
        return map.get(word.toLowerCase());
    }
}

