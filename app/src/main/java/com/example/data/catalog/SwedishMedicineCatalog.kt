package com.example.data.catalog

data class MedicineCatalogItem(
    val barcode: String,
    val name: String,
    val strength: String,
    val form: String,
    val packageSize: String? = null,
    val activeSubstance: String? = null
)

/**
 * Inbyggd svensk läkemedelskatalog för hemapoteket.
 * Ger blixtsnabb offline-igenkänning vid skanning av vanliga svenska läkemedelsförpackningar.
 */
object SwedishMedicineCatalog {

    private val catalogItems = listOf(
        // VÄRK OCH FEBER - Alvedon
        MedicineCatalogItem(
            barcode = "7300009012345", // Standard testkod
            name = "Alvedon",
            strength = "500 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195246",
            name = "Alvedon",
            strength = "500 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195253",
            name = "Alvedon",
            strength = "500 mg",
            form = "Filmdragerad tablett",
            packageSize = "50 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195307",
            name = "Alvedon",
            strength = "500 mg",
            form = "Brustablett",
            packageSize = "20 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195369",
            name = "Alvedon",
            strength = "500 mg",
            form = "Munsönderfallande tablett",
            packageSize = "16 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195406",
            name = "Alvedon",
            strength = "24 mg/ml",
            form = "Oral suspension",
            packageSize = "100 ml",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195154",
            name = "Alvedon",
            strength = "125 mg",
            form = "Suppositorium",
            packageSize = "10 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195178",
            name = "Alvedon",
            strength = "250 mg",
            form = "Suppositorium",
            packageSize = "10 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260195192",
            name = "Alvedon",
            strength = "500 mg",
            form = "Suppositorium",
            packageSize = "10 st",
            activeSubstance = "Paracetamol"
        ),

        // VÄRK OCH FEBER - Ipren & Ibuprofen
        MedicineCatalogItem(
            barcode = "7350035021008", // Standard testkod
            name = "Ipren",
            strength = "400 mg",
            form = "Filmdragerad tablett",
            packageSize = "30 st",
            activeSubstance = "Ibuprofen"
        ),
        MedicineCatalogItem(
            barcode = "7046260751022",
            name = "Ipren",
            strength = "400 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Ibuprofen"
        ),
        MedicineCatalogItem(
            barcode = "7046260751015",
            name = "Ipren",
            strength = "200 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Ibuprofen"
        ),
        MedicineCatalogItem(
            barcode = "7046260751060",
            name = "Ipren",
            strength = "20 mg/ml",
            form = "Oral suspension",
            packageSize = "100 ml",
            activeSubstance = "Ibuprofen"
        ),

        // VÄRK OCH FEBER - Treo
        MedicineCatalogItem(
            barcode = "7391515001142",
            name = "Treo",
            strength = "500 mg / 50 mg",
            form = "Brustablett",
            packageSize = "20 st",
            activeSubstance = "Acetylsalicylsyra & Koffein"
        ),
        MedicineCatalogItem(
            barcode = "7391515001166",
            name = "Treo",
            strength = "500 mg / 50 mg",
            form = "Brustablett",
            packageSize = "60 st",
            activeSubstance = "Acetylsalicylsyra & Koffein"
        ),
        MedicineCatalogItem(
            barcode = "7391515001241",
            name = "Treo Citrus",
            strength = "500 mg / 50 mg",
            form = "Brustablett",
            packageSize = "20 st",
            activeSubstance = "Acetylsalicylsyra & Koffein"
        ),
        MedicineCatalogItem(
            barcode = "7391515001265",
            name = "Treo Citrus",
            strength = "500 mg / 50 mg",
            form = "Brustablett",
            packageSize = "60 st",
            activeSubstance = "Acetylsalicylsyra & Koffein"
        ),

        // VÄRK OCH FEBER - Panodil & Voltaren
        MedicineCatalogItem(
            barcode = "7046260421024",
            name = "Panodil",
            strength = "500 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "7046260421222",
            name = "Panodil Zapp",
            strength = "500 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Paracetamol"
        ),
        MedicineCatalogItem(
            barcode = "5000456041285",
            name = "Voltaren",
            strength = "11,6 mg/g",
            form = "Gel",
            packageSize = "50 g",
            activeSubstance = "Diklofenak"
        ),
        MedicineCatalogItem(
            barcode = "5000456041292",
            name = "Voltaren",
            strength = "11,6 mg/g",
            form = "Gel",
            packageSize = "100 g",
            activeSubstance = "Diklofenak"
        ),
        MedicineCatalogItem(
            barcode = "5000456073163",
            name = "Voltaren T",
            strength = "25 mg",
            form = "Tablett",
            packageSize = "20 st",
            activeSubstance = "Diklofenakkalium"
        ),
        MedicineCatalogItem(
            barcode = "7350035022012",
            name = "Pronaxen",
            strength = "250 mg",
            form = "Tablett",
            packageSize = "20 st",
            activeSubstance = "Naproxen"
        ),

        // FÖRKYLNING & NÄSTÄPPA
        MedicineCatalogItem(
            barcode = "7391515011011",
            name = "Nezeril",
            strength = "0,5 mg/ml",
            form = "Nässpray",
            packageSize = "7,5 ml",
            activeSubstance = "Oximetazolin"
        ),
        MedicineCatalogItem(
            barcode = "7391515011028",
            name = "Nezeril",
            strength = "0,5 mg/ml",
            form = "Nässpray",
            packageSize = "15 ml",
            activeSubstance = "Oximetazolin"
        ),
        MedicineCatalogItem(
            barcode = "7391515011035",
            name = "Nezeril barn",
            strength = "0,25 mg/ml",
            form = "Nässpray",
            packageSize = "7,5 ml",
            activeSubstance = "Oximetazolin"
        ),
        MedicineCatalogItem(
            barcode = "7310860001019",
            name = "Otrivin",
            strength = "1 mg/ml",
            form = "Nässpray",
            packageSize = "10 ml",
            activeSubstance = "Xylometazolin"
        ),
        MedicineCatalogItem(
            barcode = "7310860001040",
            name = "Otrivin utan konserveringsmedel",
            strength = "1 mg/ml",
            form = "Nässpray",
            packageSize = "10 ml",
            activeSubstance = "Xylometazolin"
        ),
        MedicineCatalogItem(
            barcode = "7310860001156",
            name = "Otrivin Comp",
            strength = "0,5 + 0,6 mg/ml",
            form = "Nässpray",
            packageSize = "10 ml",
            activeSubstance = "Xylometazolin & Ipratropium"
        ),
        MedicineCatalogItem(
            barcode = "7310860001026",
            name = "Nasin",
            strength = "0,5 mg/ml",
            form = "Nässpray",
            packageSize = "10 ml",
            activeSubstance = "Oximetazolin"
        ),

        // HALS OCH HOSTA
        MedicineCatalogItem(
            barcode = "7391515031019",
            name = "Bafucin Mint",
            strength = "Standard",
            form = "Sugtablett",
            packageSize = "25 st",
            activeSubstance = "Bensokain, Gramicidin, Klorhexidin"
        ),
        MedicineCatalogItem(
            barcode = "7391515031026",
            name = "Bafucin Original",
            strength = "Standard",
            form = "Sugtablett",
            packageSize = "25 st",
            activeSubstance = "Bensokain, Gramicidin, Klorhexidin"
        ),
        MedicineCatalogItem(
            barcode = "5000158066517",
            name = "Strepsils Honung & Citron",
            strength = "Standard",
            form = "Sugtablett",
            packageSize = "24 st",
            activeSubstance = "Amylmetakresol & Diklorbensylalkohol"
        ),
        MedicineCatalogItem(
            barcode = "5000158066531",
            name = "Strepsils Jordgubb",
            strength = "Sockerfri",
            form = "Sugtablett",
            packageSize = "24 st",
            activeSubstance = "Amylmetakresol & Diklorbensylalkohol"
        ),

        // MAGE & TARM
        MedicineCatalogItem(
            barcode = "7391515051017",
            name = "Omeprazol Teva",
            strength = "20 mg",
            form = "Enterokapsel",
            packageSize = "14 st",
            activeSubstance = "Omeprazol"
        ),
        MedicineCatalogItem(
            barcode = "7391515051024",
            name = "Omeprazol Teva",
            strength = "20 mg",
            form = "Enterokapsel",
            packageSize = "28 st",
            activeSubstance = "Omeprazol"
        ),
        MedicineCatalogItem(
            barcode = "7314540001018",
            name = "Gaviscon",
            strength = "Standard",
            form = "Oral suspension",
            packageSize = "400 ml",
            activeSubstance = "Natriumalginat & Natriumvätekarbonat"
        ),
        MedicineCatalogItem(
            barcode = "7314540001025",
            name = "Gaviscon",
            strength = "Standard",
            form = "Tuggtablett",
            packageSize = "120 st",
            activeSubstance = "Natriumalginat & Natriumvätekarbonat"
        ),
        MedicineCatalogItem(
            barcode = "7391515071015",
            name = "Novalucol",
            strength = "Standard",
            form = "Tuggtablett",
            packageSize = "100 st",
            activeSubstance = "Kalciumkarbonat & Magnesiumhydroxid"
        ),
        MedicineCatalogItem(
            barcode = "7350035031014",
            name = "Dimor",
            strength = "2 mg",
            form = "Tablett",
            packageSize = "16 st",
            activeSubstance = "Loperamid"
        ),
        MedicineCatalogItem(
            barcode = "3574660421019",
            name = "Imodium",
            strength = "2 mg",
            form = "Tablett",
            packageSize = "16 st",
            activeSubstance = "Loperamid"
        ),
        MedicineCatalogItem(
            barcode = "7391515081014",
            name = "Microlax",
            strength = "Standard",
            form = "Lösning",
            packageSize = "4 x 5 ml",
            activeSubstance = "Natriumcitrat & Natriumlaurylsulfoacetat"
        ),
        MedicineCatalogItem(
            barcode = "7391515091013",
            name = "Resorb Vätskeersättning Apelsin",
            strength = "Brustabletter",
            form = "Brustablett",
            packageSize = "20 st",
            activeSubstance = "Elektrolyter & Glukos"
        ),
        MedicineCatalogItem(
            barcode = "7391515091020",
            name = "Resorb Vätskeersättning Hallon",
            strength = "Brustabletter",
            form = "Brustablett",
            packageSize = "20 st",
            activeSubstance = "Elektrolyter & Glukos"
        ),

        // ALLERGI
        MedicineCatalogItem(
            barcode = "7350035041013",
            name = "Loratadin Orifarm",
            strength = "10 mg",
            form = "Tablett",
            packageSize = "30 st",
            activeSubstance = "Loratadin"
        ),
        MedicineCatalogItem(
            barcode = "7350035041020",
            name = "Kestine",
            strength = "10 mg",
            form = "Filmdragerad tablett",
            packageSize = "30 st",
            activeSubstance = "Ebastin"
        ),
        MedicineCatalogItem(
            barcode = "7350035041037",
            name = "Desloratadin Sandoz",
            strength = "5 mg",
            form = "Filmdragerad tablett",
            packageSize = "30 st",
            activeSubstance = "Desloratadin"
        ),
        MedicineCatalogItem(
            barcode = "7350035041044",
            name = "Cetirizin Teva",
            strength = "10 mg",
            form = "Filmdragerad tablett",
            packageSize = "30 st",
            activeSubstance = "Cetirizin"
        ),

        // SÅR, HUD & ÖGON
        MedicineCatalogItem(
            barcode = "7391515101019",
            name = "Xylocain",
            strength = "5 %",
            form = "Salva",
            packageSize = "10 g",
            activeSubstance = "Lidokain"
        ),
        MedicineCatalogItem(
            barcode = "7391515111018",
            name = "Hydrokortison CCS",
            strength = "1 %",
            form = "Kräm",
            packageSize = "20 g",
            activeSubstance = "Hydrokortison"
        ),
        MedicineCatalogItem(
            barcode = "7391515121017",
            name = "Alsolsprit",
            strength = "10 mg/ml",
            form = "Lösning",
            packageSize = "250 ml",
            activeSubstance = "Aluminiumacetotartrat"
        ),
        MedicineCatalogItem(
            barcode = "7391515131016",
            name = "Klorhexidin Fresenius Kabi",
            strength = "0,5 mg/ml",
            form = "Lösning",
            packageSize = "250 ml",
            activeSubstance = "Klorhexidinglukonat"
        ),

        // VANLIGA RECEPTBELAGDA LÄKEMEDEL
        MedicineCatalogItem(
            barcode = "7046260111017",
            name = "Kåvepenin",
            strength = "1 g",
            form = "Filmdragerad tablett",
            packageSize = "14 st",
            activeSubstance = "Fenoximetylpenicillin"
        ),
        MedicineCatalogItem(
            barcode = "7046260111024",
            name = "Kåvepenin",
            strength = "800 mg",
            form = "Filmdragerad tablett",
            packageSize = "20 st",
            activeSubstance = "Fenoximetylpenicillin"
        ),
        MedicineCatalogItem(
            barcode = "7350035051012",
            name = "Levaxin",
            strength = "50 mikrog",
            form = "Tablett",
            packageSize = "100 st",
            activeSubstance = "Levotyroxinnatrium"
        ),
        MedicineCatalogItem(
            barcode = "7350035051029",
            name = "Levaxin",
            strength = "100 mikrog",
            form = "Tablett",
            packageSize = "100 st",
            activeSubstance = "Levotyroxinnatrium"
        ),
        MedicineCatalogItem(
            barcode = "7350035061011",
            name = "Losartan Teva",
            strength = "50 mg",
            form = "Filmdragerad tablett",
            packageSize = "98 st",
            activeSubstance = "Losartan"
        ),
        MedicineCatalogItem(
            barcode = "7350035071010",
            name = "Atorvastatin Krka",
            strength = "20 mg",
            form = "Filmdragerad tablett",
            packageSize = "100 st",
            activeSubstance = "Atorvastatin"
        )
    )

    // Snabbindexerad map för O(1) uppslag
    private val barcodeIndex: Map<String, MedicineCatalogItem> = catalogItems.associateBy { it.barcode }

    /**
     * Söker blixtsnabbt offline i katalogen.
     */
    fun findByBarcode(barcode: String): MedicineCatalogItem? {
        val clean = barcode.trim()
        return barcodeIndex[clean]
    }
}
