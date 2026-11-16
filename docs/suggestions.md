# Suggestions, learning and autocorrect

## The suggestion strip

Three slots: best suggestion in the middle (bold), second on its left, third on
its right. Every slot animates on its own: only a slot whose text changed
fades the old text out and the new one in, in place, with a 4dp upward move,
and slots that change together start 35 ms apart. With system animations off
the change is instant. Durations and distances are resources in `:ime`.

## Where suggestions come from

- `LanguageSeed`: a small hand-picked word list per language (about 200 English
  words, about 200 Bangla words, plus next-word rules). It gives suggestions on
  a fresh keyboard. It is not a frequency dictionary and has not been reviewed
  by a linguist.
- Learned words: every word the user finishes typing is stored with how often
  and when it was used, and which word followed which (`:data`, Room).
- `Ranker` scores learned words by use count with a gentle recency decay
  (half-life 30 days, never below a quarter of the score). Seed words score at
  most 0.5, so a word the user has typed once outranks every seed word. A word
  that followed the previous word before counts double.
- `SuggestionProvider` completes the word being typed or predicts the next
  word. While a Bangla word is being typed it offers completions, the spelling
  as typed, an alternative spelling (one the user chose before first) and the
  Latin text, for English words typed in Bangla mode.

`WordStore` (in `:engine`) is all the engine knows about storage; `:data`
implements it with Room.

## Autocorrect (English only)

`MisspellingTable` holds about 150 well-known typos and missing apostrophes
(`teh` -> `the`, `dont` -> `don't`, `i` -> `I`). Only mistakes that are almost
never intended are listed, so a correct word is never changed.

- A correction happens when the word is ended by space, enter or `, . ! ? ; :`.
  A single letter (`i`) is only corrected before a space or a new line, so
  `i.e.` is untouched.
- Backspace right after a correction restores the typed word. That word is then
  stored (DataStore) and never corrected again. Resetting learned data forgets it.
- Capitalization is kept (`Teh` -> `The`). Bangla typing is never corrected.
- Off in password, email and web address fields, in fields that ask for no
  suggestions, and when the user turns it off in the app.

Not implemented: correction by spelling distance. That needs a full dictionary,
because with a small word list a correct word such as `cat` would be changed to
`can`. A dictionary with a suitable open licence has to be chosen first.
