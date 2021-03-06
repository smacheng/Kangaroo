package kangaroo.data

import kangaroo.AppUtils
import kangaroo.Textbook

/**
 * Pulls in data from Amazon.com about textbooks.
 */
class AmazonFetchService {

    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
    static transactional = false

    def lookupAllTextbooks() {
        println "Fetching detailed textbook data from amazon.com..."
        AppUtils.runAndTime("Amazon details fetched") {
            cleanUpGorm()
            Textbook.findAllByMatchedOnAmazon(false).each { textbook ->
                lookupTextbookInfo(textbook)
            }
        }
    }

    def lookupTextbookInfo(Textbook textbook) {

        Textbook.withTransaction {
            try {
                textbook = textbook.merge()
                println "Looking up Amazon.com details for ${textbook} (${textbook.id})..."
                def page = AppUtils.cleanAndConvertToXml(new URL(textbook.amazonLink).text)

                textbook.matchedOnAmazon = true;
                textbook.amazonPrice = AppUtils.parseCurrency(AppUtils.findInNode(page) { it.@id == "actualPriceValue" }.b.toString());
                textbook.imageUrl = AppUtils.findInNode(page) { node -> node.@id == "prodImageCell" }.a.img.@src;
                if (!textbook.imageUrl)
                    textbook.imageUrl = AppUtils.findInNode(page) { node -> node.@id == "prodImageCell" }.img.@src;
                textbook.save(flush: true);
                println "| - \"${textbook.title}\" is ${textbook.matchedOnAmazon ? "" : "NOT"} matched"
                cleanUpGorm()
            }
            catch (Exception e) { println "Failed (${e})" }
        }
    }

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
