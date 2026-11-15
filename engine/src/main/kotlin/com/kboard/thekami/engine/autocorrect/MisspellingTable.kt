package com.kboard.thekami.engine.autocorrect

/**
 * Well-known English typos and missing apostrophes with their fixes, one
 * `typo>fix` rule per line. Only mistakes that are almost never intended are
 * listed, so a correct word is never changed. Keys are lowercase.
 */
internal object MisspellingTable {
    private const val RULES = """
teh>the
hte>the
adn>and
nad>and
taht>that
thier>their
thsi>this
tihs>this
wiht>with
waht>what
wnat>want
jsut>just
yuo>you
abotu>about
ahve>have
alwasy>always
knwo>know
konw>know
tiem>time
shoudl>should
woudl>would
coudl>could
recieve>receive
recieved>received
recieving>receiving
wierd>weird
definately>definitely
definatly>definitely
seperate>separate
occured>occurred
ocurred>occurred
untill>until
tommorow>tomorrow
tomorow>tomorrow
tommorrow>tomorrow
begining>beginning
beleive>believe
freind>friend
truely>truly
wich>which
whcih>which
becuase>because
becasue>because
alot>a lot
acommodate>accommodate
accomodate>accommodate
adress>address
apparant>apparent
calender>calendar
cemetary>cemetery
collegue>colleague
commited>committed
comming>coming
concious>conscious
embarass>embarrass
enviroment>environment
existance>existence
foriegn>foreign
goverment>government
grammer>grammar
happend>happened
immediatly>immediately
independant>independent
knowlege>knowledge
maintainance>maintenance
neccessary>necessary
necesary>necessary
noticable>noticeable
occassion>occasion
persistant>persistent
posession>possession
prefered>preferred
publically>publicly
realy>really
refered>referred
relevent>relevant
rythm>rhythm
sucessful>successful
succesful>successful
suprise>surprise
tendancy>tendency
tounge>tongue
unfortunatly>unfortunately
vaccum>vacuum
ofcourse>of course
infact>in fact
aswell>as well
thankyou>thank you
thnaks>thanks
recomend>recommend
reccomend>recommend
probaly>probably
proably>probably
libary>library
febuary>february
writting>writing
diffrent>different
diffrence>difference
intresting>interesting
intrested>interested
familar>familiar
beautifull>beautiful
peice>piece
sincerly>sincerely
feild>field
cheif>chief
i>I
i'm>I'm
i'll>I'll
i've>I've
i'd>I'd
im>I'm
ive>I've
dont>don't
cant>can't
wont>won't
didnt>didn't
doesnt>doesn't
isnt>isn't
wasnt>wasn't
werent>weren't
arent>aren't
couldnt>couldn't
shouldnt>shouldn't
wouldnt>wouldn't
havent>haven't
hasnt>hasn't
hadnt>hadn't
thats>that's
whats>what's
youre>you're
theyre>they're
heres>here's
"""

    val rules: Map<String, String> = parse(RULES)

    private fun parse(text: String): Map<String, String> = buildMap {
        text.lines().map { it.trim() }.filter { it.isNotEmpty() }.forEach { line ->
            val separator = line.indexOf('>')
            require(separator > 0) { "Bad rule: $line" }
            val typo = line.substring(0, separator)
            require(typo !in this) { "Duplicate rule: $typo" }
            put(typo, line.substring(separator + 1))
        }
    }
}
