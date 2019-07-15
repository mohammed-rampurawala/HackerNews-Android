package com.mr.myapplication

import com.mr.myapplication.network.IHackerNewsAPI
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TestHackerApplication : HackerApplication() {

    @Mock
    lateinit var hackerNewsAPI: IHackerNewsAPI

    override fun initComponent() {
        println("Init of ${this.javaClass.name} Called")
        if (!this::hackerNewsAPI.isInitialized) {
            MockitoAnnotations.initMocks(this)
        }
        hackerComponent = DaggerTestHackerComponent.builder().providerModule(
            ProviderModule(
                hackerNewsAPI
            )
        ).build()
    }
}