# Ergast
#### com.jyothi.ergast
This package holds view related class for this application.

> Ergast.java – Application class for this application.
> MainActivity.java – Activity class for this application. This class holds View for this application. Displays, driver’s information present in Ergast, in a list. This class interacts with ViewModel class.
> ItemAdapter.java – Adapter class for the RecyclerView. 

#### com.jyothi.ergast.model
This package holds model classes for this application

> DriverStub.java, DriverTable.java, ItemResponse.java, MRData.java – Model classes for Gson parser
> MainViewModel.java – ViewModel class for the View. This class interacts with View class.


#### com.jyothi.ergast.network
This package holds network related classes for this application.

> NetworkQueue.java – Singleton class for Volly network queue.
> RequestFetcher.java – Creates rest url and network request for getting drivers information from Ergast API.


#### com.jyothi.ergast.parser
This package holds parser classes for the REST response.

> Parser.java – REST API response parser class.


#### com.jyothi.ergast.interfaces
This package holds contract interfaces for the application.


#### com.jyothi.ergast.data
This package holds Room database classes for this application.


#### com.jyothi.ergast.util
This package holds utility class for this application.

### TODO
> Writing Automation tests
> Get search results from Adapter list rather than getting it from DB.

