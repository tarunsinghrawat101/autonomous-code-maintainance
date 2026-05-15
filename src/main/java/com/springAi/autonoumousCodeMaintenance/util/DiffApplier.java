package com.springAi.autonoumousCodeMaintenance.util;

import com.springAi.autonoumousCodeMaintenance.model.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffApplier {
    public String applyPatch(String original, List<Patch> patches) {

        if (patches == null || patches.isEmpty()) {
            return original;
        }

        List<String> lines = new ArrayList<>(Arrays.asList(original.split("\n")));

        // sort reverse to avoid shifting line indexes
        patches.sort((a, b) -> Integer.compare(b.line(), a.line()));

        for (Patch patch : patches) {

            int idx = patch.line() - 1;

            if (idx < 0 || idx >= lines.size()) {
                System.out.println("Invalid line: " + patch.line());
                continue;
            }

            String current = lines.get(idx);

            // safety check
            if (current.trim().equals(patch.oldCode().trim())) {
                lines.set(idx, patch.newCode());
            } else {
                System.out.println("Skipping mismatch at line " + patch.line());
            }
        }

        return String.join("\n", lines);
    }
}
