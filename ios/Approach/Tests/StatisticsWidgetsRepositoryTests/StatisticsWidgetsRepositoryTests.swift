import DatabaseModelsLibrary
import DatabaseServiceInterface
import Dependencies
import Foundation
import GRDB
@testable import ModelsLibrary
import StatisticsRepository
import StatisticsRepositoryInterface
@testable import StatisticsWidgetsRepository
@testable import StatisticsWidgetsRepositoryInterface
import TestDatabaseUtilitiesLibrary
import Testing
import TestUtilitiesLibrary
import TestUtilitiesPackageLibrary

@Suite("StatisticsWidgetsRepository", .tags(.repository))
struct StatisticsWidgetsRepositoryTests {

	// MARK: Fetch

	@Suite("fetchAll", .tags(.dependencies, .grdb))
	struct FetchAllTests {
		@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

		@Test("Fetches all widgets", .tags(.unit))
		func fetchesAllWidgets() async throws {
			// Given a database with 4 widgets
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 2)
			let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
			let widget4 = StatisticsWidget.Database.mock(id: UUID(3), created: Date(timeIntervalSince1970: 4), priority: 4)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3, widget4]))

			// Fetching the widgets
			let widgets = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				statisticsWidgets.fetchAll(forContext: nil)
			}
			var iterator = widgets.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expected: [StatisticsWidget.Configuration] = [
				.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(1), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(3), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			]

			// Returns all of the widgets
			#expect(fetched == expected)
		}

		@Test("Fetches widgets for context", .tags(.unit))
		func fetchesWidgetsForContext() async throws {
			// Given a database with 4 widgets
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
			let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), context: "context", priority: 3)
			let widget4 = StatisticsWidget.Database.mock(id: UUID(3), created: Date(timeIntervalSince1970: 4), context: "other", priority: 4)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3, widget4]))

			// Fetching the widgets
			let widgets = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				statisticsWidgets.fetchAll(forContext: "context")
			}
			var iterator = widgets.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expected: [StatisticsWidget.Configuration] = [
				.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			]

			// Returns the widgets for the context
			#expect(fetched == expected)
		}

		@Test("Fetches widgets by priority", .tags(.unit))
		func fetchesWidgetsByPriority() async throws {
			// Given a database with 4 widgets
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 2)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 1)
			let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3]))

			// Fetching the widgets
			let widgets = withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				statisticsWidgets.fetchAll(forContext: "context")
			}
			var iterator = widgets.makeAsyncIterator()
			let fetched = try await iterator.next()

			let expected: [StatisticsWidget.Configuration] = [
				.init(id: UUID(1), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
				.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			]

			// Returns the widgets in order
			#expect(fetched == expected)
		}
	}

	// MARK: Load Sources

	@Suite("loadSources", .tags(.dependencies, .grdb))
	struct LoadSourcesTests {
		@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

		@Test("Loads sources for bowler", .tags(.unit))
		func loadsSourcesForBowler() async throws {
			let db = try generatePopulatedDatabase()
			let source: StatisticsWidget.Source = .bowler(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: nil
			))
		}

		@Test("Loads sources for league", .tags(.unit))
		func loadsSourcesForLeague() async throws {
			let db = try generatePopulatedDatabase()
			let source: StatisticsWidget.Source = .league(UUID(0))

			let sources = try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsRepository.self] = .liveValue
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.loadSources(source)
			}

			#expect(sources == .init(
				bowler: .init(id: UUID(0), name: "Joseph"),
				league: .init(id: UUID(0), name: "Majors, 2022-23")
			))
		}
	}

	// MARK: Update Priorities

	@Suite("updatePriorities", .tags(.dependencies, .grdb))
	struct UpdatePrioritiesTests {
		@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

		@Test("Sets new priorities", .tags(.unit))
		func setsNewPriorities() async throws {
			// Given a database with 3 widgets
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 2)
			let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3]))

			// Updating the priorities
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.updatePriorities([UUID(2), UUID(0), UUID(1)])
			}

			let updated1 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated1?.priority == 1)

			let updated2 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(1)) }
			#expect(updated2?.priority == 2)

			let updated3 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(2)) }
			#expect(updated3?.priority == 0)
		}

		@Test("With different contexts, does nothing", .tags(.unit))
		func withDifferentContextsDoesNothing() async throws {
			// Given a database with widgets with different contexts
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2]))

			// Updating the priorities throws an error
			await #expect(throws: StatisticsWidget.ContextError.self) {
				try await withDependencies {
					$0[DatabaseService.self].reader = { @Sendable in db }
					$0[StatisticsWidgetsRepository.self] = .liveValue
				} operation: {
					try await statisticsWidgets.updatePriorities([UUID(0), UUID(1)])
				}
			}
		}
	}

	// MARK: Create

	@Suite("create", .tags(.dependencies, .grdb))
	struct CreateTests {
		@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

		@Test("Throws error if widget exists", .tags(.unit))
		func throwsErrorIfWidgetExists() async throws {
			// Given a database with an existing widget
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1]))

			// Create the widget
			await assertThrowsError(ofType: DatabaseError.self) {
				let create = StatisticsWidget.Create(id: UUID(0), created: Date(), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average Pins Left on Deck", context: "", priority: 0)
				try await withDependencies {
					$0[DatabaseService.self].writer = { @Sendable in db }
					$0[StatisticsWidgetsRepository.self] = .liveValue
				} operation: {
					try await statisticsWidgets.create(create)
				}
			}

			// Does not insert any records
			let widgetCount = try await db.read { try StatisticsWidget.Database.fetchCount($0) }
			#expect(widgetCount == 1)

			// Does not update the database
			let updated = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.timeline == .past3Months)
			#expect(updated?.statistic == "Average")
		}

		@Test("Creates widget if it does not exist", .tags(.unit))
		func createsWidgetIfItDoesNotExist() async throws {
			// Given a database with no widgets
			let db = try initializeApproachDatabase(withBowlers: .default, withStatisticsWidgets: nil)

			// Creating a widget
			let create = StatisticsWidget.Create(id: UUID(0), created: Date(), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average Pins Left on Deck", context: "", priority: 0)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.create(create)
			}

			// Inserted the record
			let exists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
			#expect(exists)

			// Updates the database
			let updated = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
			#expect(updated?.id == UUID(0))
			#expect(updated?.timeline == .allTime)
			#expect(updated?.statistic == "Average Pins Left on Deck")
		}
	}

	// MARK: Delete

	@Suite("delete", .tags(.dependencies, .grdb))
	struct DeleteTests {
		@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

		@Test("Deletes widget when ID exists", .tags(.unit))
		func deletesWidgetWhenIDExists() async throws {
			// Given a database with 2 bowlers
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
			let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1, widget2]))

			// Deleting the first bowler
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.delete(UUID(0))
			}

			// Updates the database
			let deletedExists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
			#expect(!deletedExists)

			// And leaves the other widget intact
			let otherExists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(1)) }
			#expect(otherExists)
		}

		@Test("Does nothing when ID does not exist", .tags(.unit))
		func doesNothingWhenIDDoesNotExist() async throws {
			// Given a database with 1 widget
			let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
			let db = try initializeApproachDatabase(withStatisticsWidgets: .custom([widget1]))

			// Deleting a non-existent widget
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await statisticsWidgets.delete(UUID(1))
			}

			// Leaves the widget
			let exists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
			#expect(exists)
		}
	}
}
