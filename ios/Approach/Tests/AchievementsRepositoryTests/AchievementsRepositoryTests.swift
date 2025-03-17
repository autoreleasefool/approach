@testable import AchievementsRepository
@testable import AchievementsRepositoryInterface
import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("AchievementsRepository", .tags(.repository))
struct AchievementsRepositoryTests {

	// MARK: list

	@Suite("list", .tags(.dependencies, .grdb))
	struct ListTests {
		@Dependency(AchievementsRepository.self) var achievements

		@Test("Lists all achievements", .tags(.unit))
		func listAllAchievements() async throws {
			Issue.record("Unimplemented")
		}

		@Test("Does not list hidden achievements", .tags(.unit))
		func doesNotListHiddenAchievements() async throws {
			Issue.record("Unimplemented")
		}

		@Test("Correctly counts achievements earned multiple times", .tags(.unit))
		func correctlyCountsAchievementsEarnedMultipleTimes() async throws {
			Issue.record("Unimplemented")
		}

		@Test("Returns correct firstEarnedDate for achievements earned multiple times", .tags(.unit))
		func correctFirstEarnedDate_forAchievementsEarnedMultipleTimes() async throws {
			Issue.record("Unimplemented")
		}
	}

	// MARK: observeNewAchievements

	@Suite("observeNewAchievements", .tags(.dependencies, .grdb))
	struct ObserveNewAchievementsTests {
		@Dependency(AchievementsRepository.self) var achievements

		@Test("Finishes immediately when feature flag is disabled", .tags(.unit))
		func finishesImmediately_whenFeatureFlagIsDisabled() async throws {
			Issue.record("Unimplemented")
		}

		@Test("Sends new achievements", .tags(.unit))
		func sendsNewAchievements() async throws {
			Issue.record("Unimplemented")
		}

		@Test("Does not report existing achievements", .tags(.unit))
		func doesNotReportExistingAchievements() async throws {
			Issue.record("Unimplemented")
		}

	}
}
