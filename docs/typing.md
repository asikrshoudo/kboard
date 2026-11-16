# Typing, layouts and Bangla phonetic input

## Languages

English (US) and Bangla (phonetic) are implemented. The globe key switches
between them, the language is shown on the space bar and remembered. Both use
the same Latin QWERTY keys; in Bangla mode the full stop key types the dari (।).

## Pages

- Letters, a symbols page and a second symbols page (including the Taka sign ৳).
- Number and phone fields open on the symbols page, where the digits are.
- Sentence auto-capitalization, only where the field asks for it (English only).
- Backspace repeats while held.

## Layout settings

Stored as `KeyboardAppearance` in DataStore and applied by the keyboard as soon
as they change:

- **Number row:** a row of digits above the letters (Latin digits, also in
  Bangla mode; Bangla digits are not implemented).
- **Height:** 80 to 140 percent of the default key height, in steps of 5. The
  value is kept inside that range when it is saved and when it is read.
- **Key preview:** the bubble above a pressed key can be turned off.

The keyboard is as tall as the letters page on every page (4 rows, or 5 with
the number row). A page with fewer rows, such as the symbol pages, shares that
height, so its keys are taller and switching pages never moves the app above.

In the companion app the slider updates a drawing of the key grid while it is
dragged and saves the value when the finger lifts. The real keyboard runs in the
same process, so it changes as soon as the value is saved. How the system
resizes the keyboard window while it is open has not been checked on many
devices yet.

## Bangla phonetic input

Typing `ami` shows আমি. Latin letters are collected by `PhoneticComposer`,
converted by `PhoneticTransliterator` and shown in the text field's composing
region. Space, enter, punctuation, a page switch or a language switch keeps the
word as ordinary text. If the text before the cursor no longer matches the
word, the user moved the cursor and a new word starts.

Rules follow Avro Phonetic: capitals change the sound (`t` ত, `T` ট), adjacent
consonants form conjuncts (`rokto` রক্ত), `o` is the inherent vowel and `O` the
ো sign, `ng` is ং.

Not implemented: ঃ ঁ ৎ, Bangla digits, long-press popups, the traditional
(non-phonetic) layout.
