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
package freedots.musicxml;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;

import freedots.math.Fraction;
import freedots.music.Event;

import org.w3c.dom.Element;

public final class Sound implements Event {
  private final Fraction moment;
  private final Element element;

  public Sound(final Element element, final Fraction moment) {
    this.element = element;
    this.moment = moment;
  }

  /** Creates a MIDI tempo change event if this Sound element specifies tempo.
   * @return a MetaMessage or null, if no tempo attribute was found
   */
  public MetaMessage getTempoMessage() {
    if (element.hasAttribute("tempo")) {
      final float tempo = Float.parseFloat(element.getAttribute("tempo"));
      int midiTempo = Math.round((float)60000.0 / tempo * 1000);
      final MetaMessage message = new MetaMessage();
      byte[] bytes = new byte[3];
      bytes[0] = (byte) (midiTempo / 0X10000);
      midiTempo %= 0X10000;
      bytes[1] = (byte) (midiTempo / 0X100);
      midiTempo %= 0X100;
      bytes[2] = (byte) midiTempo;
      try {
        message.setMessage(0X51, bytes, bytes.length);
        return message;
      } catch (InvalidMidiDataException e) {}
    }
    return null;
  }

  /** Gets the MIDI velocity if this sound element specifies it.
   * @return an Integer in the range of 0 to 127, or null of velocity was not
   *         specified
   */
  public Integer getMidiVelocity() {
    if (element.hasAttribute("dynamics")) {
      Float dynamics = Float.parseFloat(element.getAttribute("dynamics"));
      return new Integer(Math.round(((float)90 / (float)100) * dynamics));
    }
    return null;
  }
  public Fraction getMoment() { return moment; }
  public boolean equalsIgnoreOffset(Event object) {
    if (object instanceof Sound) {
      Sound other = (Sound)object;
      if (element.equals(other.element)) return true;
    }
    return false;
  }
}

