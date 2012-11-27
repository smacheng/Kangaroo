package kangaroo

/**
 * Represents a class term at Austin College (ie, "Fall 2012", "January 2015").
 * Terms have classes associated with them.
 */
class Term implements Serializable {

    static hasMany = [courses: Course]
    static transients = ['fullDescription', 'season', 'year']
    static expandedSeasons = ["FA": "Fall", "SP": "Spring", "JA": "January", "SU": "Summer"]

    String id // The registrar short code ("11FA", "12SP", "13JA") - everything is derived from this one field!

    // Define the current term (for calendars) and default search term (for the search table).
    static final String CURRENT_TERM_CODE = "12FA", DEFAULT_SEARCH_TERM_CODE = "13SP"

    static constraints = {
        id(maxSize: 4)
    }

    static mapping = {
        id(column: 'code', generator: 'assigned')
    }

    // Returns the current term.
    static Term getCurrentTerm() { return Term.get(CURRENT_TERM_CODE) }

    // Returns the current term.
    static Term getDefaultSearchTerm() { return Term.get(DEFAULT_SEARCH_TERM_CODE) }

    /**
     * Formats this term nicely (i.e., "Fall 2012").
     */
    String getFullDescription() {
        return "$season $year"
    }

    /**
     * Returns this term's season as a full word (e.g. "Fall", "Spring").
     */
    String getSeason() {
        return expandedSeasons[id[2..-1]];
    }

    /**
     * Returns this term's year (e.g., 2011).
     */
    int getYear() {
        return 2000 + Integer.parseInt(id[0..1]);
    }

    /**
     * Finds or creates the term of the given code.
     */
    static Term findOrCreate(String code) {
        if (Term.exists(code))
            return Term.get(code);
        else
            return new Term(id: code).save(flush: true);
    }

    String toString() { return fullDescription; }

    static def saveFromJsonObject(object) {
        def term = new Term()
        term.id = object.id;
        return AppUtils.saveSafely(term.save());
    }

    def toJsonObject() { [id: id, description: fullDescription, year: year, season: season, isActive: id == Term.CURRENT_TERM_CODE] }
}
