package pl.gosub.elementaryhttp

// try with
// scalac -print HelloExercises.scala
// scalac -Xshow-phases
// scalac -Xprint:patmat HelloExercises.scala
// scalac -Xprint:typer HelloExercises.scala

trait User {
  def name: String
  def score: Int
  def balance: Int
}

class RegularUser(val name: String, val score: Int, val balance: Int) extends User {
  def ~> (adminTag: Boolean) = if(adminTag) AdminUser(name, score, balance) else this
}
class AdminUser(val name: String, val score: Int, val balance: Int) extends User
class MultiNamedUser(val name: String, val score: Int, val balance: Int, val name2: String, val name3: String) extends User

object RegularUser {
  def apply(name: String, score: Int, balance: Int) = new RegularUser(name, score, balance)
  def unapply(user: RegularUser): Option[String] = Some(user.name)
//  def unapply(user: RegularUser): Option[(String, Int, Int)] = Some(user.name, user.score, user.balance)
}

object AdminUser {
  def apply(name: String, score: Int, balance: Int) = new AdminUser(name, score, balance)
  def unapply(user: AdminUser): Option[String] = Some(user.name)
//  def unapply(user: RegularUser): Option[(String, Int, Int)] = Some(user.name, user.score, user.balance)
}

object MultiNamedUser {
  def apply(name: String, score: Int, balance: Int, name2: String, name3: String) = new MultiNamedUser(name, score, balance, name2, name3)
  //def unapply(user: MultiNamedUser): Option[String] = Some(user.name) // uncommenting this yields an error
  def unapplySeq(user: MultiNamedUser): Option[(String, Int, Int, Seq[String])] = Some(user.name, user.score, user.balance, Seq(user.name2, user.name3))
}

object highScore {
  def unapply(u: User): Boolean = u.score > 75
}

object PromoteUser {
  def >~ (u: RegularUser): AdminUser = AdminUser(u.name, u.score, u.balance)
  val toAdmin = true
}

object HelloExercises extends App {
  def printUser(u: User): Unit = {
    u match {
      case RegularUser(_) => println("Just a regular")
      case user @ highScore() => println("This user is a high scorer")
      case AdminUser(n) => println(s"Bow to $n 'coz he's the Admin")
      case MultiNamedUser(n, _, _, _*) => println(s"A multinameder with first name of $n")
    }
  }

  val regular = RegularUser("a", 0, 0)
  val admin = AdminUser("b", 0, 0)
  val scorer = AdminUser("c", 100, 0)
  val multi = MultiNamedUser("d", 0, 0, "aa", "bb")

  printUser(regular)
  printUser(admin)
  printUser(scorer)
  printUser(multi)

  val admin2 = PromoteUser >~ regular // unary operator plus the parens free syntax
  printUser(admin2)

  val admin3 = regular ~> PromoteUser.toAdmin
  printUser(admin3)

}
