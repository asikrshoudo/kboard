package com.kboard.thekami.engine.suggest

/**
 * About 200 common English words, one line per first letter, most common
 * first. Hand-picked, not a frequency dictionary. There is no line for x.
 */
internal object EnglishSeed {
    private const val WORDS = """
a and are at as all about after also any again always am an another around ask away
b be but by because before been being both back between best better big
c can could come call came can't couldn't car change check city class close clear
d do does did day done don't doesn't didn't down during different doing
e even every each enough else early either ever
f for from first find friend family feel few found free
g get go good going great give got group gone
h have has had he her his how here help hello hi home
i i'm i'll i've it it's in is if into its isn't important idea include
j just job join joke july june
k know keep kind key kids kept
l like look little last let let's long life later
m my me more most make much many may might morning
n not no now new need never next night name number
o of on or one our out only other over often ok okay
p people please put part place point problem
q question quite quick quality quickly quiet
r right really request reason read result room
s so some see she should still say such same school
t the to that that's this they there then their time than thank thanks they're today
u up us use used under until
v very version video visit value various
w with was what what's we we're will would when who which want where why work well won't wasn't
y you your yes yet year yesterday you're you'll you've
z zero zone zoom zebra zip
"""

    private const val FOLLOWERS = """
i: am will have think want can
i'm: not going sorry fine so
you: are can have will want know
the: same best most first
thank: you
thanks: for a
how: are to do is
good: morning night evening luck
what: is are do about
are: you we they
do: you not it
let: me us you
have: a to been
will: be you not
to: be the do see
in: the a my
of: the a course
it: is was will
this: is was
can: you i we
please: let send call help
see: you the
not: sure now
going: to home
want: to
need: to a
there: is are
that: is was
"""

    private const val STARTERS = "i the thanks hi hello ok"

    val seed: LanguageSeed = LanguageSeed.parse(WORDS, FOLLOWERS, STARTERS)
}
