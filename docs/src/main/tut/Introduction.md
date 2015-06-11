The AutoLift library is about enhancing the experience of using Scala such that it allows code to be written which arbitrarily nests basic types and use more complex type patterns without having to resort to the nuclear approach of Monad Transformers. Moreover, all attempts have been made to hide the complexities of the implementation as well as any direct dependencies. Users do not have to use or even understand the ideas behind Scalaz to enjoy the benefits.

Several points about this documentation:
 * All code examples are checked at compile using the [tut SBT Plugin](https://github.com/tpolecat/tut)
 * Sections are divided into three portions: Lifters, Folders and Transformers
 * It's a living, breathing document so pull requests are appreciated to help make this more approachable.