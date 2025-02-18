package ru.nmedvedev.anekdoter.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.nmedvedev.anekdoter.model.Rate
import java.util.UUID

interface RateRepository : JpaRepository<Rate, UUID>