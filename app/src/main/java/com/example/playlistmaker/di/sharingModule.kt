package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val sharingModule = module {
    factory<ExternalNavigator> { (context: Context) ->
        ExternalNavigatorImpl(context)
    }

    factory<SharingInteractor> { (context: Context) ->
        SharingInteractorImpl(
            externalNavigator = get { parametersOf(context) },
            context = context
        )
    }
}