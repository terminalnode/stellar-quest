# Stellar Quest
These are my solutions to [Stellar Quest](https://quest.stellar.org/) problems.
In short, Stellar Quest is a competition/game with problems to solve on the Stellar
cryptocurrency platform. If problems are solved quickly enough, the solver gets a
reward in XLM (Stellar Lumens, the native currency of Stellar).

All quests take place on the Stellar testnet so no real money are ever at risk
(though rewards are paid out on the mainnet and vary from 500 to 20 XLM). To be
eligible for rewards one has to enter the competition within an hour of it being
announced.

Quests vary a lot in difficulty, for some quests you just need to send a payment
to some address. For some you need to make a series of transactions triggering
the more sophisticated features of the Stellar chain.

## Spoiler policy
I won't push solutions until they've been out for a day or more. The timestamps on the
commits may be the same day though.

## Missing solutions
This repository is an afterthought. As mentioned the quests vary a lot in difficulty,
and many can actually be solved on [Stellar Laboratory](https://laboratory.stellar.org/)
without any code whatsoever. In fact, all quests can probably be solved this way, but
using the API is more interesting to me.

I solved all of the first set using Stellar Laboratory before discovering the SDK, so those
solutions are currently missing. Perhaps if a feature to redo the challenges is ever published
I'll add those as well.

## Why is this a Ktor project?
I mostly do backend web development, and I wanted to try out Ktor even if it doesn't make
much sense for this to be an API. It also provides an easy way for me to run specific
solutions by triggering their endpoints. ¯\_(ツ)_/¯

## Positions
Bear in mind that some of these are released in the middle of the night, the exact release time
depends on the quest. To give everyone a chance (I assume) the time of day that they are released
varies a little.
 - **(Set 3, quest 2)** 13th place (80 XLM)
 - **(Set 3, quest 3)** 6th place (200 XLM)
 - **(Set 3, quest 4)** 1st place (500 XLM)