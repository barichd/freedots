/* -*- c-basic-offset: 2; indent-tabs-mode: nil; -*- */
/*
 * FreeDots -- MusicXML to braille music transcription
 *
 * Copyright 2008-2010 Mario Lang  All Rights Reserved.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details (a copy is included in the LICENSE.txt file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License
 * along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This file is maintained by Mario Lang <mlang@delysid.org>.
 */
package freedots.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.sound.midi.MidiSystem;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.JOptionPane;

import freedots.braille.BrailleEncoding;
import freedots.musicxml.MIDISequence;
import freedots.musicxml.Score;
import freedots.transcription.Transcriber;

/**
 * Action for saving the currently open MusicXML document.
 * The dialog allows for export to MIDI and Unicode as well as NABCC braille.
 */
@SuppressWarnings("serial")
public final class FileSaveAsAction extends javax.swing.AbstractAction {
  private Main gui;

  /** Constructs a new action object for saving files.
   * @param gui is used to retrieve the currently loaded score object
   */
  public FileSaveAsAction(final Main gui) {
    super("Save as...");
    this.gui = gui;
    putValue(SHORT_DESCRIPTION, "Export to braille or standard MIDI file");
    putValue(MNEMONIC_KEY, KeyEvent.VK_A);
  }
  /** Uses [@link JFileChooser} to prompt the user for a file name to save to.
   * <p>
   * The output file format is determined according to the file extension.
   * @see java.awt.event.ActionListener#actionPerformed
   */
  public void actionPerformed(ActionEvent event) {
    Score score = gui.getScore();
    if (score != null) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().matches(".*\\.brl");
        }
        @Override
        public String getDescription() {
          return "Unicode braille (*.brl)";
        }
      });
      fileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().matches(".*\\.brf");
        }
        @Override
        public String getDescription() {
          return "Legacy BRF (*.brf)";
        }
      });
      fileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().matches(".*\\.mid");
        }
        @Override
        public String getDescription() {
          return "Standard MIDI file (*.mid)";
        }
      });
      fileChooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().matches(".*\\.xml");
        }
        @Override
        public String getDescription() {
          return "MusicXML file (*.xml)";
        }
      });
      if (fileChooser.showSaveDialog(gui) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        String ext = getExtension(file);
        if ("mid".equals(ext)) {
          exportToMidi(score, file);
        } else if ("brl".equals(ext)) {
          exportToUnicodeBraille(gui.getTranscriber(), file);
        } else if ("brf".equals(ext)) {
          exportToBRF(gui.getTranscriber(), file);
        } else if ("xml".equals(ext)) {
          exportToMusicXML(score, file);
        } else if (ext != null) {
          String message = "Unknown file extension '"+ext+"'";
          JOptionPane.showMessageDialog(gui, message, "Alert",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  /** Saves the given score to MusicXML format.
   * @param score is the score to save
   * @param file identifies the target filename
   */
  private static void exportToMusicXML(Score score, File file) {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
      score.save(fileOutputStream);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /** Saves the given score to as a MIDI file.
   * @param score is the score to convert to MIDI
   * @param file identifies the target filename
   */
  private static void exportToMidi(Score score, File file) {
    FileOutputStream fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(file);
      try {
        MidiSystem.write(new MIDISequence(score), 1, fileOutputStream);
      } catch (Exception exception) {
        exception.printStackTrace();
      } finally {
        fileOutputStream.close();
      }
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /** Saves the braille trnascription in Unicode encoding.
   */
  private static void
  exportToUnicodeBraille(Transcriber transcriber, File file) {
    Writer fileWriter = null;
    try {
      try {
        fileWriter = new FileWriter(file);
        fileWriter.write(transcriber.toString());
      } finally {
        fileWriter.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Saves the braille trnascription result with BRF encoding.
   */
  private static void
  exportToBRF(Transcriber transcriber, File file) {
    Writer fileWriter = null;
    try {
      try {
        fileWriter = new FileWriter(file);
        fileWriter.append(transcriber.toString(BrailleEncoding.NorthAmericanBrailleComputerCode));
      } finally {
        fileWriter.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String getExtension(File file) {
    String ext = null;
    String fileName = file.getName();
    int index = fileName.lastIndexOf('.');

    if (index > 0 && index < fileName.length() - 1)
      ext = fileName.substring(index + 1).toLowerCase();

    return ext;
  }
}
