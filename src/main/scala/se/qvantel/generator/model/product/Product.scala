package se.qvantel.generator.model.product

case class Product(name: String, serviceType: String, campaignId: String, startHour : String, endHour : String, points : List[Point])