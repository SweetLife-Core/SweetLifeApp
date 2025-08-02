package com.amikom.sweetlife.di

import android.app.Application
import android.content.Context
import com.amikom.sweetlife.BuildConfig
import com.amikom.sweetlife.data.datastore.DataStoreManager
import com.amikom.sweetlife.data.manager.LocalAuthUserManagerImpl
import com.amikom.sweetlife.data.manager.LocalUserManagerImpl
import com.amikom.sweetlife.data.remote.interceptor.AuthInterceptor
import com.amikom.sweetlife.data.remote.repository.AuthRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.DashboardRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.EditHealthRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.EditProfileRepositoryImpl
import com.amikom.sweetlife.data.remote.repository.ProfileRepositoryImpl
import com.amikom.sweetlife.data.remote.retrofit.AuthApiService
import com.amikom.sweetlife.data.remote.retrofit.FeatureApiService
import com.amikom.sweetlife.domain.manager.LocalAuthUserManager
import com.amikom.sweetlife.domain.manager.LocalUserManager
import com.amikom.sweetlife.domain.manager.SessionManager
import com.amikom.sweetlife.domain.repository.AuthRepository
import com.amikom.sweetlife.domain.repository.DashboardRepository
import com.amikom.sweetlife.domain.repository.EditHealthDataRepository
import com.amikom.sweetlife.domain.repository.EditProfileRepository
import com.amikom.sweetlife.domain.repository.ProfileRepository
import com.amikom.sweetlife.domain.usecases.app_entry.AppEntryUseCases
import com.amikom.sweetlife.domain.usecases.app_entry.GetAppThemeMode
import com.amikom.sweetlife.domain.usecases.app_entry.ReadAppEntry
import com.amikom.sweetlife.domain.usecases.app_entry.SaveAppEntry
import com.amikom.sweetlife.domain.usecases.app_entry.UpdateAppThemeMode
import com.amikom.sweetlife.domain.usecases.auth.AuthUseCases
import com.amikom.sweetlife.domain.usecases.auth.CheckHasHealthProfile
import com.amikom.sweetlife.domain.usecases.auth.CheckIsUserLogin
import com.amikom.sweetlife.domain.usecases.auth.ForgotUserPassword
import com.amikom.sweetlife.domain.usecases.auth.LoginAction
import com.amikom.sweetlife.domain.usecases.auth.LogoutAction
import com.amikom.sweetlife.domain.usecases.auth.ReadUserAllToken
import com.amikom.sweetlife.domain.usecases.auth.RefreshNewTokenAction
import com.amikom.sweetlife.domain.usecases.auth.RegisterAction
import com.amikom.sweetlife.domain.usecases.auth.SaveHealthProfile
import com.amikom.sweetlife.domain.usecases.auth.SaveNewToken
import com.amikom.sweetlife.domain.usecases.auth.SaveUserInfoLogin
import com.amikom.sweetlife.domain.usecases.dashboard.DashboardUseCases
import com.amikom.sweetlife.domain.usecases.dashboard.FetchData
import com.amikom.sweetlife.domain.usecases.dashboard.FindFood
import com.amikom.sweetlife.domain.usecases.dashboard.SaveFood
import com.amikom.sweetlife.domain.usecases.dashboard.ScanFood
import com.amikom.sweetlife.domain.usecases.profile.CreateHealthProfile
import com.amikom.sweetlife.domain.usecases.profile.FetchDataHealthProfile
import com.amikom.sweetlife.domain.usecases.profile.FetchDataProfile
import com.amikom.sweetlife.domain.usecases.profile.ProfileUseCases
import com.amikom.sweetlife.domain.usecases.profile.UpdateDataProfile
import com.amikom.sweetlife.util.AppExecutors
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val baseUrl: String = if(BuildConfig.DEBUG) BuildConfig.BASE_URL_DEV else BuildConfig.BASE_URL_PROD

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application
    ) : LocalUserManager = LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager
    ) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager),
        updateAppThemeMode = UpdateAppThemeMode(localUserManager),
        getAppThemeMode = GetAppThemeMode(localUserManager)
    )


    @Provides
    @Singleton
    fun provideLocalAuthUserManager(
        application: Application,
        dataStoreManager: DataStoreManager
    ): LocalAuthUserManager {
        return LocalAuthUserManagerImpl(application, dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideAuthApi() : AuthApiService {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFeatureApi(
        authInterceptor: AuthInterceptor,
    ) : FeatureApiService {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
//            .baseUrl("http://192.168.100.191:3000/")
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(FeatureApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppExecutors(): AppExecutors = AppExecutors()

    @Provides
    @Singleton
    fun provideSessionManager(
        localAuthUserManager: LocalAuthUserManager
    ): SessionManager {
        return SessionManager(localAuthUserManager)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApiService,
        appExecutors: AppExecutors,
        authUserManager: LocalAuthUserManager
    ): AuthRepository = AuthRepositoryImpl(authApi, appExecutors, authUserManager)

    @Provides
    @Singleton
    fun provideDashboardRepository(
        featureApiService: FeatureApiService,
        appExecutors: AppExecutors
    ): DashboardRepository = DashboardRepositoryImpl(featureApiService, appExecutors)


    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        featureApiService: FeatureApiService,
        appExecutors: AppExecutors
    ): ProfileRepository = ProfileRepositoryImpl(context, featureApiService, appExecutors)

    @Provides
    @Singleton
    fun provideLoginUseCases(
        authRepository: AuthRepository,
        localAuthUserManager: LocalAuthUserManager
    ) : AuthUseCases {
        return AuthUseCases(
            login = LoginAction(authRepository = authRepository),
            checkIsUserLogin = CheckIsUserLogin(localAuthUserManager = localAuthUserManager),
            saveUserInfoLogin = SaveUserInfoLogin(localAuthUserManager = localAuthUserManager),
            readUserAllToken = ReadUserAllToken(localAuthUserManager = localAuthUserManager),
            register = RegisterAction(authRepository = authRepository),
            forgotPassword = ForgotUserPassword(authRepository = authRepository),
            refreshNewToken = RefreshNewTokenAction(authRepository = authRepository),
            saveNewToken = SaveNewToken(localAuthUserManager = localAuthUserManager),
            checkHasHealthProfile = CheckHasHealthProfile(localAuthUserManager = localAuthUserManager),
            saveHealthProfile = SaveHealthProfile(localAuthUserManager = localAuthUserManager),
            logout = LogoutAction(authRepository = authRepository,
                                  localAuthUserManager = localAuthUserManager),
            readUserId = com.amikom.sweetlife.domain.usecases.auth.ReadUserId(localAuthUserManager = localAuthUserManager)
        )
    }

    @Provides
    @Singleton
    fun provideDashboardUseCases(
        dashboardRepository: DashboardRepository,
    ) : DashboardUseCases {
        return DashboardUseCases(
            fetchData = FetchData(dashboardRepository = dashboardRepository),
            scanFood = ScanFood(dashboardRepository = dashboardRepository),
            findFood = FindFood(dashboardRepository = dashboardRepository),
            saveFood = SaveFood(dashboardRepository = dashboardRepository)
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideProfileUseCases(
        profileRepository: ProfileRepository,
    ) : ProfileUseCases {
        return ProfileUseCases(
            fetchDataProfile = FetchDataProfile(profileRepository = profileRepository),
            fetchDataHealthProfile = FetchDataHealthProfile(profileRepository = profileRepository),
            updateDataProfile = UpdateDataProfile(profileRepository = profileRepository),
            createHealthProfile = CreateHealthProfile(profileRepository = profileRepository)
        )
    }

    @Provides
    @Singleton
    fun provideEditProfileRepository(
        featureApiService: FeatureApiService
    ): EditProfileRepository = EditProfileRepositoryImpl(featureApiService)

    @Provides
    @Singleton
    fun provideEditHealthDataRepository(
        featureApiService: FeatureApiService
    ): EditHealthDataRepository = EditHealthRepositoryImpl(featureApiService)
}