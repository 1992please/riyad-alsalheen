package com.nader.riyadalsalheen.ui.screens

//onNavigateToDoor = { doorId ->
//    coroutineScope.launch {
//        getFirstHadithIdInDoor(doorId)?.let {
//            onNavigateToHadith(it)
//        }
//    }

//@Preview(
//    name = "Default Light Theme",
//    showBackground = true,
//    locale = "ar"
//)
//@Preview(
//    name = "Dark Theme",
//    showBackground = true,
//    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
//    locale = "ar"
//)
//@Composable
//fun NavigationDrawerPreview() {
//
//    // Sample Books data
//    val sampleBooks = listOf(
//        Book(id = 1, title = "كتاب الإخلاص"),
//        Book(id = 2, title = "كتاب التوبة"),
//        Book(id = 3, title = "كتاب الصبر"),
//        Book(id = 4, title = "كتاب الصدق"),
//        Book(id = 5, title = "كتاب المراقبة"),
//        Book(id = 6, title = "كتاب التقوى"),
//        Book(id = 7, title = "كتاب اليقين والتوكل"),
//        Book(id = 8, title = "كتاب الذكر ودعاء الله"),
//        Book(id = 9, title = "كتاب قراءة القرآن"),
//        Book(id = 10, title = "كتاب الصلاة"),
//        Book(id = 11, title = "كتاب آداب الطعام"),
//        Book(id = 12, title = "كتاب الأمر بالمعروف والنهي عن المنكر")
//    )
//
//    // Sample Doors data
//    val sampleDoors = listOf(
//        // Doors for Book 1 (الإخلاص)
//        Door(id = 1, bookId = 1, title = "باب الإخلاص وإحضار النية"),
//        Door(id = 2, bookId = 1, title = "باب فضل الإخلاص"),
//        Door(id = 3, bookId = 1, title = "باب النية في الطاعات"),
//
//        // Doors for Book 2 (التوبة)
//        Door(id = 4, bookId = 2, title = "باب وجوب التوبة"),
//        Door(id = 5, bookId = 2, title = "باب فضل التوبة"),
//        Door(id = 6, bookId = 2, title = "باب بيان كثرة طرق الخير"),
//
//        // Doors for Book 3 (الصبر)
//        Door(id = 7, bookId = 3, title = "باب فضل الصبر"),
//        Door(id = 8, bookId = 3, title = "باب الصبر على المصائب"),
//        Door(id = 9, bookId = 3, title = "باب الصبر على الطاعة"),
//
//        // Doors for Book 4 (الصدق)
//        Door(id = 10, bookId = 4, title = "باب قول الحق"),
//        Door(id = 11, bookId = 4, title = "باب الوفاء بالوعد"),
//
//        // Doors for Book 5 (المراقبة)
//        Door(id = 12, bookId = 5, title = "باب مراقبة الله تعالى"),
//        Door(id = 13, bookId = 5, title = "باب التقوى"),
//
//        // Doors for Book 6 (التقوى)
//        Door(id = 14, bookId = 6, title = "باب اليقين والتوكل"),
//        Door(id = 15, bookId = 6, title = "باب الاستقامة"),
//        Door(id = 16, bookId = 6, title = "باب التفكر في عظمة الله"),
//
//        // Doors for Book 7 (اليقين والتوكل)
//        Door(id = 17, bookId = 7, title = "باب اليقين"),
//        Door(id = 18, bookId = 7, title = "باب التوكل على الله"),
//        Door(id = 19, bookId = 7, title = "باب الثقة بالله"),
//
//        // Doors for Book 8 (الذكر ودعاء الله)
//        Door(id = 20, bookId = 8, title = "باب فضل الذكر والحث عليه"),
//        Door(id = 21, bookId = 8, title = "باب فضل التهليل والتسبيح"),
//        Door(id = 22, bookId = 8, title = "باب فضل الدعاء"),
//        Door(id = 23, bookId = 8, title = "باب دعوات مستجابة"),
//
//        // Doors for Book 9 (قراءة القرآن)
//        Door(id = 24, bookId = 9, title = "باب فضل القرآن"),
//        Door(id = 25, bookId = 9, title = "باب الأمر بتعاهد القرآن"),
//        Door(id = 26, bookId = 9, title = "باب فضل الوضوء"),
//
//        // Doors for Book 10 (الصلاة)
//        Door(id = 27, bookId = 10, title = "باب فضل الصلاة"),
//        Door(id = 28, bookId = 10, title = "باب فضل صلاة الجماعة"),
//        Door(id = 29, bookId = 10, title = "باب فضل صلاة الفجر والعصر"),
//        Door(id = 30, bookId = 10, title = "باب فضل المشي إلى المساجد"),
//
//        // Doors for Book 11 (آداب الطعام)
//        Door(id = 31, bookId = 11, title = "باب آداب الطعام"),
//        Door(id = 32, bookId = 11, title = "باب التسمية في أول الطعام"),
//        Door(id = 33, bookId = 11, title = "باب ما يقول إذا فرغ من الطعام"),
//
//        // Doors for Book 12 (الأمر بالمعروف والنهي عن المنكر)
//        Door(id = 34, bookId = 12, title = "باب وجوب الأمر بالمعروف"),
//        Door(id = 35, bookId = 12, title = "باب النهي عن المنكر"),
//        Door(id = 36, bookId = 12, title = "باب آداب الأمر بالمعروف")
//    )
//
//    RiyadalsalheenTheme {
//        NavigationDrawer(
//            books = sampleBooks,
//            doors = sampleDoors,
//            currentBookId = 8,  // Currently in "كتاب الذكر ودعاء الله"
//            currentDoorId = 22,  // Currently in "باب فضل الدعاء"
//            hadithCount = 1896,
//            onNavigateToDoor = { /* Preview - no action */ },
//            onClose = { /* Preview - no action */ }
//        )
//    }
//}