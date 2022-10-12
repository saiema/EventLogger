package ar.edu.unrc.exa.dc.mfis.event_logger.text_diff;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TextDiffUtils {

    public static List<String> diff(String original, String revised) {
        List<String> originalLines = Arrays.asList(original.split("\\r?\\n"));
        List<String> revisedLines = Arrays.asList(revised.split("\\r?\\n"));
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .mergeOriginalRevised(false)
                .inlineDiffByWord(false)
                .oldTag(f -> "~")
                .newTag(f -> "**")
                .build();
        List<DiffRow> diffRows = generator.generateDiffRows(originalLines, revisedLines);
        List<String> differences = new LinkedList<>();
        for (DiffRow diffRow : diffRows) {
            differences.add(diffRow.toString());
        }
        return differences;
    }

}
