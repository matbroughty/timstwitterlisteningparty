package com.timstwitterlisteningparty.tools

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestStuff {


  @Test
  fun testHash(){

    Assertions.assertEquals(-307841062,"Teenage Fanclub".trim().toLowerCase().plus("Bandwagonesque".trim().toLowerCase()).hashCode())

    Assertions.assertEquals(-307841062,"Teenage Fanclub".trim().toLowerCase().plus("Bandwagonesque".trim().toLowerCase()).hashCode())

  }


}
