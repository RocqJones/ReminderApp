package com.rocqjones.reminderapp

import com.rocqjones.reminderapp.data.DataSource
import com.rocqjones.reminderapp.models.ComposeRandomItem
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DataSourceUnitTest : TestCase() {

    private val actualPlants = DataSource.plants
    private lateinit var expectedPlant1 : ComposeRandomItem
    private lateinit var expectedPlant2 : ComposeRandomItem

    @Before
    override fun setUp() {
        expectedPlant1 = ComposeRandomItem(
            name = "Aloe Vera",
            schedule = "Monthly",
            type = "Succulent",
            description = "Aloe vera is a succulent plant species of the genus Aloe. It is cultivated for agricultural and medicinal uses."
        )
        expectedPlant2 = ComposeRandomItem(
            name = "Bamboo Palm",
            schedule = "Weekly",
            type = "Palm",
            description = "The Bamboo Palm, also known as the Reed Palm, is a medium-sized palm native to South America. It is a popular houseplant and can grow up to 12 feet tall."
        )
    }

    @Test
    fun testExpectedPlant1() {
        Assert.assertEquals(expectedPlant1, actualPlants[0])
    }

    @Test
    fun testExpectedPlant2() {
        Assert.assertEquals(expectedPlant2, actualPlants[1])
    }
}