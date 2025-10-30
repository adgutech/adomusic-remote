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

import com.spotify.protocol.types.Track;

import java.util.ArrayList;

public class Lyrics {

    private static final ArrayList<Class<? extends Lyrics>> FORMATS = new ArrayList<>();

    static {
        Lyrics.FORMATS.add(SynchronizedLyricsLRC.class);
    }

    public String data;
    public Track track;
    protected boolean parsed = false;
    protected boolean valid = false;

    public static boolean isSynchronized(String data) {
        for (Class<? extends Lyrics> format : Lyrics.FORMATS) {
            try {
                Lyrics lyrics = format.newInstance().setData(null, data);
                if (lyrics.isValid()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Lyrics parse(Track track, String data) {
        for (Class<? extends Lyrics> format : Lyrics.FORMATS) {
            try {
                Lyrics lyrics = format.newInstance().setData(track, data);
                if (lyrics.isValid()) {
                    return lyrics.parse(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Lyrics().setData(track, data).parse(false);
    }

    public String getText() {
        return this.data.trim().replaceAll("(\r?\n){3,}", "\r\n\r\n");
    }

    public boolean isSynchronized() {
        return false;
    }

    public boolean isValid() {
        this.parse(true);
        return this.valid;
    }

    public Lyrics parse(boolean check) {
        this.valid = true;
        this.parsed = true;
        return this;
    }

    public Lyrics setData(Track track, String data) {
        this.track = track;
        this.data = data;
        return this;
    }
}
