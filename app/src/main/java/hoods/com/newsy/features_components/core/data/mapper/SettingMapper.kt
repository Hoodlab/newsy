package hoods.com.newsy.features_components.core.data.mapper

import hoods.com.newsy.features_components.core.data.local.models.SettingsDto
import hoods.com.newsy.features_components.core.domain.mapper.Mapper
import hoods.com.newsy.features_components.core.domain.models.Setting

class SettingMapper : Mapper<SettingsDto, Setting> {
    override fun toModel(value: SettingsDto): Setting {
        return value.run {
            Setting(
                preferredCountryIndex = preferredCountry,
                preferredLanguageIndex = preferredLanguage
            )
        }
    }

    override fun fromModel(value: Setting): SettingsDto {
        return value.run {
            SettingsDto(
                preferredLanguage = preferredLanguageIndex,
                preferredCountry = preferredCountryIndex
            )
        }
    }
}