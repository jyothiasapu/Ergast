# Ergast

This is an android application displays drivers informatrion in a list, from Ergast Drivers API. And Application provides searchview, to search about a driver present in the list view. To reduce the network interactions, storing the fetched drivers into a local database.

Used MVVM Architectural pattern to design this application. And used Android architectural components(LiveData, ViewModel and Room database) to implement this application.


#### com.jyothi.ergast
This package holds view related class for this application.

1. Ergast.java – Application class for this application.
2. MainActivity.java – Activity class for this application. This class holds View for this application. Displays, driver’s information present in Ergast, in a list. This class interacts with ViewModel class.
3. ErgastComponent.java – A Component class for Dagger2.

#### com.jyothi.ergast.adapter
1. DriverAdapter.java - Adapter class for showing Drivers information in RecyclerView.

#### com.jyothi.ergast.data
This package holds Room database classes for this application.

#### com.jyothi.ergast.di
This package holds all dependency Injection API Modules.

#### com.jyothi.ergast.interfaces
This package holds contract interfaces for the application.

#### com.jyothi.ergast.model
This package holds model classes for this application

1. DriverStub.java, DriverTable.java, ItemResponse.java, MRData.java – Model classes for Gson parser

#### com.jyothi.ergast.network
This package holds network related classes for this application.

1. ApiService.java – Retrofit2 Api service class.

#### com.jyothi.ergast.util
This package holds utility class for this application.

#### com.jyothi.ergast.viewholder
1. DriverViewHolder.java – ViewHolder class for displaying Driver information in RecyclerView.

#### com.jyothi.ergast.viewmodel
This is VM of M-V-VM architecture.
1. MainViewModel.java - Holds pagedList of Drivers information


### TODO
1. Automation tests

