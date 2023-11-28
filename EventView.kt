@Composable
    fun EventView(
        dayId: Long,
        hasDayEntityBeenChanged: MutableState<Boolean>
    ) {
        val eventList by remember { mutableStateOf(mDayViewModel.getEventsByDayId(dayId).events) }
        val newEventTitle = remember { mutableStateOf("") }
        val newEventCategory = remember { mutableStateOf("General") }
        var isEditing by remember { mutableStateOf(false) }
        var isDropdownExpanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text("Event List")

            eventList.forEach { event ->
                var eventCategory by remember { mutableStateOf(event.category) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val icon = getCategoryIcon(eventCategory)
                    Icon(imageVector = icon, contentDescription = null)
                    Text(event.title)
                }
            }

            HorizontalDivider()

            if (isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isEditing) {
                    // Category dropdown
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .clickable {
                                isDropdownExpanded = !isDropdownExpanded
                            }
                    ) {
                        Text(newEventCategory.value)
                        // Dropdown menu for selecting the category
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = {
                                isDropdownExpanded = false
                            }
                        ) {
                            val categoryList =
                                listOf("General", "Party", "Sports", "Meeting", "Food")

                            categoryList.forEach { category ->
                                if (category == newEventCategory.value) {
                                    return@forEach
                                }
                                DropdownMenuItem(
                                    {
                                        Text(category, Modifier.padding(8.dp))
                                    },
                                    onClick = {
                                        newEventCategory.value = category
                                        hasDayEntityBeenChanged.value = true
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                // Event title input field
                InlineTextEditor(
                    data = newEventTitle,
                    hasDayEntityBeenChanged = hasDayEntityBeenChanged
                ) {
                    if (newEventTitle.value.isNotEmpty()) {
                        mDayViewModel.saveEventEntity(
                            EventEntity(
                                dayForeignId = dayId,
                                title = newEventTitle.value,
                                category = newEventCategory.value
                            )
                        )
                    }
                    isEditing = false
                    newEventCategory.value = "General"
                    newEventTitle.value = ""
                }
            }
        }else {
                    IconButton(onClick = {
                        isEditing = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new event"
                        )
                    }
                }
        }
    }