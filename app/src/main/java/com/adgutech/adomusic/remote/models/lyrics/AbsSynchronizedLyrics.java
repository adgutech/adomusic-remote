/*
 * Copyright (C) 2022-2025 Adolfo Gutiérrez <adgutech@gmail.com>
 * and Contributors.
 *
 * This file is part of Adgutech.
 *
 *  Adgutech is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.adgutech.adomusic.remote.models.lyrics;

import android.util.SparseArray;

public abstract class AbsSynchronizedLyrics extends Lyrics {

    private static final int TIME_OFFSET_MS =
            500; // time adjustment to display line before it actually starts

    protected final SparseArray<String> lines = new SparseArray<>();

    protected int offset = 0;

    public String getLine(int time) {
        time += offset + AbsSynchronizedLyrics.TIME_OFFSET_MS;

        int lastLineTime = lines.keyAt(0);

        for (int i = 0; i < lines.size(); i++) {
            int lineTime = lines.keyAt(i);

            if (time >= lineTime) {
                lastLineTime = lineTime;
            } else {
                break;
            }
        }

        return lines.get(lastLineTime);
    }

    @Override
    public String getText() {
        parse(false);

        if (valid) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.valueAt(i);
                sb.append(line).append("\r\n");
            }

            return sb.toString().trim().replaceAll("(\r?\n){3,}", "\r\n\r\n");
        }

        return super.getText();
    }

    public boolean isSynchronized() {
        return true;
    }

    public boolean isValid() {
        parse(true);
        return valid;
    }
}
