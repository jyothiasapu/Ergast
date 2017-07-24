# Ergast
#### com.jyothi.ergast
This package holds view related class for this application.

1. Ergast.java – Application class for this application.
2. MainActivity.java – Activity class for this application. This class holds View for this application. Displays, driver’s information present in Ergast, in a list. This class interacts with ViewModel class.
3. ItemAdapter.java – Adapter class for the RecyclerView. 

#### com.jyothi.ergast.model
This package holds model classes for this application

1. DriverStub.java, DriverTable.java, ItemResponse.java, MRData.java – Model classes for Gson parser
2. MainViewModel.java – ViewModel class for the View. This class interacts with View class.


#### com.jyothi.ergast.network
This package holds network related classes for this application.

1. NetworkQueue.java – Singleton class for Volly network queue.
2. RequestFetcher.java – Creates rest url and network request for getting drivers information from Ergast API.


#### com.jyothi.ergast.parser
This package holds parser classes for the REST response.

1. Parser.java – REST API response parser class.


#### com.jyothi.ergast.interfaces
This package holds contract interfaces for the application.


#### com.jyothi.ergast.data
This package holds Room database classes for this application.


#### com.jyothi.ergast.util
This package holds utility class for this application.

### TODO
1. Writing Automation tests
2. Get search results from Adapter list rather than getting it from DB, for OS versions below Android N.
3. Not handled case for orientation change. As per Google documentation, Its not a problem. But, need to verify.

