package se.qvantel.generator.utils.property.config

import java.io.InputStream
import org.json4s.{DefaultFormats, _}
import org.json4s.native.JsonMethods._
import se.qvantel.generator.model.Country

trait ProductConfig extends ApplicationConfig {
  val countriesFile = config.getString("gen.countries.file")
  val roamingChance = config.getString("gen.roaming.change").toDouble

  /**
    * Tries to open a JSON-file in the classpath with the name as described "countriesFile"
    * The method then tries to parse the contents of that file as JSON using the library Json4s.
    * It will then parse the JSON into the model.Country model, with the properties as described.
    * @return List[Int]
    */
  def getAvailableMccCodesByCountry: Map[String, Array[Int]] = {
    // Open a source file
    val res = config.getString("gen.countries.file")

    // Open a resource from the res variable. We use a resource since it can be a jar-context.
    val source : InputStream = getClass.getResourceAsStream(res)

    // Finally, read the actual contents into a string.
    val lines = scala.io.Source.fromInputStream( source ).mkString

    // For json4s, specify parse format
    implicit val format = DefaultFormats

    // Parse the contents, extract to a list of countries
    val countriesList = parse(lines.toString()).extract[List[Country]].filter(_.iso != "n/a")

    // Close source file
    source.close()

    // Take the Country.mcc and make it's own list with only the distinct values
    countriesList.map(c =>
      c.country_code ->
      countriesList
        .filter(_.country_code == c.country_code)
        .map(c => c.mcc.toInt)
        .distinct
        .toArray
    ).toMap
  }

  def getIsoMccMap: Map[String, String] = {
    // Open a source file
    val source : InputStream = getClass.getResourceAsStream(countriesFile)
    val lines = scala.io.Source.fromInputStream( source ).mkString

    // For json4s, specify parse format
    implicit val format = DefaultFormats

    // Parse the contents, extract to a list of countries
    val countriesList = parse(lines.toString()).extract[List[Country]].filter(_.iso != "n/a")

    // Close source file
    source.close()

    // Take the Country.iso and make it's own list with only the distinct values
    countriesList.groupBy(_.country_code)
      .map(_._2.head)
      .map(c => c.iso -> c.mcc)
      .toMap
  }
}


