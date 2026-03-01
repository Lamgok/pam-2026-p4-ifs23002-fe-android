package org.delcom.pam_p4_ifs23002.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_p4_ifs23002.network.sukus.service.ISukusAppContainer
import org.delcom.pam_p4_ifs23002.network.sukus.service.ISukusRepository
import org.delcom.pam_p4_ifs23002.network.sukus.service.SukusAppContainer
import org.delcom.pam_p4_ifs23002.network.sukus.service.SukusRepository


@Module
@InstallIn(SingletonComponent::class)
object SukusModule {
    @Provides
    fun provideSukusContainer(): ISukusAppContainer {
        return SukusAppContainer()
    }

    @Provides
    fun provideSukusRepository(container: ISukusAppContainer): ISukusRepository {
        return container.sukusRepository
    }
}