## Sprawozdanie z projektu: Aplikacja "Szlaki Turystyczne"

### Wprowadzenie

Niniejsze sprawozdanie przedstawia opis aplikacji mobilnej "Szlaki Turystyczne", która została stworzona w języku programowania Kotlin, wykorzystując platformę Android. Aplikacja działa jako przewodnik turystyczny, udostępniając użytkownikom listę szlaków turystycznych wraz z ich szczegółowymi informacjami. 

### Funkcjonalność aplikacji

Aplikacja "Szlaki Turystyczne" oferuje następujące funkcjonalności:

* **Wyświetlanie listy szlaków:** Użytkownik może przeglądać listę dostępnych szlaków turystycznych. Lista jest wyświetlana w postaci siatki (grid) za pomocą widoku `RecyclerView`. Każdy szlak jest przedstawiony w formie karty (`CardView`) zawierającej obraz i nazwę.

```kotlin
// MainFragment.kt
// ...
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    recyclerView = view.findViewById(R.id.recyclerView)
    recyclerView.layoutManager = GridLayoutManager(context, 2)

    // Read data from JSON file
    val jsonString = readJsonFromFile(requireContext(), "mountains.json")
    val gson = Gson()
    val mountainList = gson.fromJson(jsonString, Array<Mountain>::class.java).toList()

    val adapter = MountainRecyclerViewAdapter(requireActivity(), mountainList, this)
    recyclerView.adapter = adapter
}
// ...
```

* **Wyświetlanie szczegółów szlaku:** Po kliknięciu na wybrany szlak użytkownik przechodzi do ekranu szczegółów. Ekran szczegółów zawiera następujące informacje:
    * Nazwę szlaku
    * Obrazek reprezentujący szlak
    * Krótki opis szlaku
    * Fragment dynamiczny stopera / zegara / krokomierza

```kotlin
// MountainDetailFragment.kt
// ...
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val mountain = arguments?.getParcelable<Mountain>("selectedMountain")

    mountainImageView = view.findViewById(R.id.mountainImage)
    mountain?.mountainImage?.let {
        val resourceId = resources.getIdentifier(it, "drawable", requireContext().packageName)
        Glide.with(requireContext()).load(resourceId).into(mountainImageView)
    }

    val mountainNameTextView = view.findViewById<TextView>(R.id.mountainNameTextView)
    mountainNameTextView.text = mountain?.name

    val heightTextView = view.findViewById<TextView>(R.id.length)
    heightTextView.text = mountain?.length + " m"

    val descriptionTextView = view.findViewById<TextView>(R.id.description)
    descriptionTextView.text = mountain?.description

    childFragmentManager.commit {
        replace(R.id.stopwatchContainer, StopwatchFragment().apply {
            arguments = Bundle().apply {
                putParcelable("selectedMountain", mountain)
            }
        })
    }
    // ...
}
// ...
```

* **Stoper / Zegar / Krokomierz:** Fragment dynamiczny zawiera trzy narzędzia do mierzenia czasu i aktywności:
    * **Stoper:** Mierzy czas od momentu uruchomienia do zatrzymania.
    * **Zegar odliczający wstecz:** Mierzy czas pozostały do przejścia danego odcinka szlaku, zgodnie z przewidywanym czasem przejścia.
    * **Krokomierz:** Zlicza liczbę kroków wykonanych w trakcie pokonywania wybranego szlaku.
    * **Funkcjonalność przycisków:**
        * **Start:** Uruchamia odliczanie w stoperze i zegarze odliczającym wstecz.
        * **Stop:** Zatrzymuje odliczanie w stoperze i zegarze odliczającym wstecz.
        * **Przerwij:** Zatrzymuje odliczanie w stoperze i zegarze odliczającym wstecz w dowolnym momencie.
    * **Zapamiętywanie wyników:** Możliwość zapamiętania wyników pomiaru czasu i krokomierza.

```kotlin
// StopwatchFragment.kt
// ...
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_stopwatch, container, false)

    chronometer = view.findViewById(R.id.chronometer)
    startButton = view.findViewById(R.id.startButton)
    stopButton = view.findViewById(R.id.stopButton)
    resetButton = view.findViewById(R.id.resetButton)
    stepCountTextView = view.findViewById(R.id.stepCounterTextView)

    sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    stepCounter = StepCounter(sensorManager, stepCountTextView)

    viewModel = ViewModelProvider(this).get(StopwatchViewModel::class.java)

    startButton.setOnClickListener { startChronometer() }
    stopButton.setOnClickListener { stopChronometer() }
    resetButton.setOnClickListener { resetChronometer() }

    updateUI()

    return view
}
// ...
private fun startChronometer() {
    if (!viewModel.isChronometerRunning) {
        chronometer.base = SystemClock.elapsedRealtime() - viewModel.chronometerBaseTime
        chronometer.start()
        viewModel.isChronometerRunning = true
    }
}

private fun stopChronometer() {
    if (viewModel.isChronometerRunning) {
        viewModel.chronometerBaseTime = SystemClock.elapsedRealtime() - chronometer.base
        chronometer.stop()
        viewModel.isChronometerRunning = false
    }
}

private fun resetChronometer() {
    viewModel.chronometerBaseTime = 0
    chronometer.base = SystemClock.elapsedRealtime()
    chronometer.stop()
    viewModel.isChronometerRunning = false

    stepCounter.resetSteps()
    updateStepCounter()
}
// ...
```

* **Przycisk FAB:** Na ekranie szczegółów znajduje się pływający przycisk akcji (FAB), który w przyszłości będzie uruchamiał aparat fotograficzny. Obecnie kliknięcie przycisku wyświetla komunikat informujący o planowanej funkcji selfie ze szlaku.

```kotlin
// MountainDetailFragment.kt
// ...
view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
    // Placeholder action for FAB button
    android.widget.Toast.makeText(requireContext(), "Selfie feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
}
// ...
```

### Architektura aplikacji

Aplikacja została zbudowana z wykorzystaniem fragmentów, co pozwala na dynamiczne zarządzanie zawartością ekranu. Główna aktywność zawiera `DrawerLayout` z szufladą nawigacyjną, która zapewnia dostęp do listy szlaków i profilu użytkownika.

* **Aktywność główna (MainActivity):**
    * Zawiera `DrawerLayout` z szufladą nawigacyjną.

```kotlin
// MainActivity.kt
// ...
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // ...

    drawerLayout = findViewById(R.id.drawer_layout)
    val navView: NavigationView = findViewById(R.id.nav_view)
    val toggle = ActionBarDrawerToggle(
        this, drawerLayout, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    // ...
}
// ...
```

    * Używa `ViewPager2` do przełączania między fragmentem głównym (MainFragment) a fragmentem profilu (ProfileFragment).

```kotlin
// MainActivity.kt
// ...
viewPager = findViewById(R.id.view_pager)
tabLayout = findViewById(R.id.tab_layout)

val adapter = ViewPagerAdapter(this)
viewPager.adapter = adapter

TabLayoutMediator(tabLayout, viewPager) { tab, position ->
    tab.text = when (position) {
        0 -> "Home"
        1 -> "Profile"
        else -> null
    }
}.attach()
// ...
```

    * `TabLayout` służy do zarządzania kartami i przełączania między fragmentami.

```kotlin
// MainActivity.kt
// ...
navView.setNavigationItemSelectedListener { menuItem ->
    when (menuItem.itemId) {
        R.id.nav_home -> {
            viewPager.currentItem = 0
        }
        R.id.nav_profile -> {
            viewPager.currentItem = 1
        }
    }
    // ...
}
// ...
```

* **Fragment główny (MainFragment):**
    * Wyświetla listę szlaków turystycznych w postaci siatki.
    * Korzysta z `RecyclerView` do wyświetlania danych i `CardView` do prezentowania poszczególnych szlaków.

```kotlin
// MainFragment.kt
// ...
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    return inflater.inflate(R.layout.fragment_main, container, false)
}
// ...
```

* **Fragment szczegółów (MountainDetailFragment):**
    * Wyświetla szczegółowe informacje o wybranym szlaku.
    * Zawiera fragment dynamiczny stopera / zegara / krokomierza.

```kotlin
// MountainDetailFragment.kt
// ...
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    return inflater.inflate(R.layout.fragment_mountain_detail, container, false)
}
// ...
```

* **Fragment stopera / zegara / krokomierza (StopwatchFragment):**
    * Zapewnia funkcjonalność stopera, zegara odliczającego wstecz i krokomierza.
    * Wykorzystuje `Chronometer` do wyświetlania czasu, `SensorManager` do odczytu danych z czujnika krokomierza i `Button` do sterowania funkcjami stopera / zegara / krokomierza.

```kotlin
// StopwatchFragment.kt
// ...
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_stopwatch, container, false)

    // ...

    return view
}
// ...
```

* **Model widoku (StopwatchViewModel):**
    * Przechowuje dane związane z działaniem stopera / zegara / krokomierza, takie jak czas bazowy i stan odliczania.

```kotlin
// StopwatchViewModel.kt
// ...
class StopwatchViewModel : ViewModel() {
    var chronometerBaseTime: Long = 0
    var isChronometerRunning: Boolean = false
}
// ...
```

* **Adapter (MountainRecyclerViewAdapter):**
    * Odpowiedzialny za wyświetlanie danych w `RecyclerView`.

```kotlin
// MountainRecyclerViewAdapter.kt
// ...
inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mountainImageView: ImageView = itemView.findViewById(R.id.mountainImage)
    val mountainNameTextView: TextView = itemView.findViewById(R.id.mountainName)

    fun bind(mountain: Mountain) {
        mountainNameTextView.text = mountain.name
        val resourceId = context.resources.getIdentifier(mountain.mountainImage, "drawable", context.packageName)
        Glide.with(context).load(resourceId).into(mountainImageView)
        itemView.setOnClickListener {
            itemClickListener.onItemClick(mountain)
        }
    }
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(context).inflate(R.layout.item_mountain, parent, false)
    return ViewHolder(view)
}

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(mountains[position])
}

override fun getItemCount(): Int {
    return mountains.size
}
// ...
```

* **Klasa danych (Mountain):**
    * Przechowuje informacje o każdym szlaku.

```kotlin
// Mountain.kt
// ...
data class Mountain(
    val name: String,
    val mountainImage: String,
    val length: String,
    val description: String,
    val time: String
) : Parcelable {
    // ...
}
// ...
```

### Implementacja

* **Fragmenty:** Aplikacja wykorzystuje fragmenty do dynamicznego zarządzania zawartością ekranu. Główną aktywność stanowi `MainActivity`, która wyświetla szufladę nawigacyjną. Wewnątrz `DrawerLayout` znajduje się `ViewPager2`, który przełącza się między `MainFragment` i `ProfileFragment`.
* **RecyclerView:** Do wyświetlania listy szlaków zastosowano `RecyclerView` w połączeniu z `CardView` do prezentacji poszczególnych szlaków. 
* **Stoper / Zegar / Krokomierz:** Fragment dynamiczny stopera / zegara / krokomierza został zaimplementowany w `StopwatchFragment`. Korzysta on z `Chronometer` do wyświetlania czasu i `SensorManager` do odczytu danych z czujnika krokomierza. 
* **Przyciski FAB:** Przycisk FAB został dodany do ekranu szczegółów, ale jego funkcjonalność jest jedynie symulowana i wyświetla komunikat.
* **Motywy:** Aplikacja korzysta z motywu Material Design, który nadaje jej spójny wygląd i wrażenia użytkownika.
* **Zmiana orientacji:** Aplikacja działa poprawnie po zmianie orientacji urządzenia.
* **Układ dla tabletów:** Aplikacja ma osobny układ dla tabletów, który wyświetla listę szlaków i szczegółowy opis obok siebie.

### Podsumowanie

Aplikacja "Szlaki Turystyczne" stanowi przykład prostą aplikację typu lista-szczegóły, która wykorzystuje fragmenty, RecyclerView, CardView, motywy i funkcjonalność stopera / zegara / krokomierza. Aplikacja działa poprawnie przy zmianie orientacji urządzenia i ma osobny układ dla tabletów. W przyszłości aplikacja będzie rozwijana o dodatkowe funkcje, takie jak możliwość robienia selfie ze szlaku.

