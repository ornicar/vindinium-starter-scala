package bot

trait Bot {
  def move(input: Input): Dir.Value
}

class RandomBot extends Bot {

  def move(input: Input) = scala.util.Random.shuffle(Dir.values.toList).head
}
