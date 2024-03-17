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
import TestUtilitiesLibrary
import XCTest

final class StatisticsWidgetsRepositoryTests: XCTestCase {
	@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

	// MARK: Fetch

	func testFetchAll_FetchesAllWidgets() async throws {
		// Given a database with 4 widgets
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 2)
		let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
		let widget4 = StatisticsWidget.Database.mock(id: UUID(3), created: Date(timeIntervalSince1970: 4), priority: 4)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3, widget4]))

		// Fetching the widgets
		let widgets = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			self.statisticsWidgets.fetchAll(forContext: nil)
		}
		var iterator = widgets.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns all of the widgets
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(1), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(3), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
		])
	}

	func testFetchAll_FilteredByContext_FetchesWidgetsForContext() async throws {
		// Given a database with 4 widgets
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
		let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), context: "context", priority: 3)
		let widget4 = StatisticsWidget.Database.mock(id: UUID(3), created: Date(timeIntervalSince1970: 4), context: "other", priority: 4)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3, widget4]))

		// Fetching the widgets
		let widgets = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			self.statisticsWidgets.fetchAll(forContext: "context")
		}
		var iterator = widgets.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the widgets for the context
		XCTAssertEqual(fetched, [
			.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
		])
	}

	func testFetchAll_OrdersByPriority() async throws {
		// Given a database with 4 widgets
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 2)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 1)
		let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3]))

		// Fetching the widgets
		let widgets = withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			self.statisticsWidgets.fetchAll(forContext: "context")
		}
		var iterator = widgets.makeAsyncIterator()
		let fetched = try await iterator.next()

		// Returns the widgets in order
		XCTAssertEqual(fetched, [
			.init(id: UUID(1), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
			.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .past3Months, statistic: "Average"),
		])
	}

	// MARK: Load Widget Sources

	func testLoadsWidgetSources_ForBowler() async throws {
		let db = try generatePopulatedDatabase()
		let source: StatisticsWidget.Source = .bowler(UUID(0))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: nil
		))
	}

	func testLoadsWidgetSources_ForLeague() async throws {
		let db = try generatePopulatedDatabase()
		let source: StatisticsWidget.Source = .league(UUID(0))

		let sources = try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[StatisticsRepository.self] = .liveValue
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.loadSources(source)
		}

		XCTAssertEqual(sources, .init(
			bowler: .init(id: UUID(0), name: "Joseph"),
			league: .init(id: UUID(0), name: "Majors, 2022-23")
		))
	}

	// MARK: Update Priorities

	func testUpdatePriorities_SetsNewPriorities() async throws {
		// Given a database with 3 widgets
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), priority: 2)
		let widget3 = StatisticsWidget.Database.mock(id: UUID(2), created: Date(timeIntervalSince1970: 3), priority: 3)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2, widget3]))

		// Updating the priorities
		try await withDependencies {
			$0[DatabaseService.self].reader = { @Sendable in db }
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.updatePriorities([UUID(2), UUID(0), UUID(1)])
		}

		let updated1 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated1?.priority, 1)

		let updated2 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(1)) }
		XCTAssertEqual(updated2?.priority, 2)

		let updated3 = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(2)) }
		XCTAssertEqual(updated3?.priority, 0)
	}

	func testUpdatePriorities_WithDifferentContexts_DoesNothing() async throws {
		// Given a database with widgets with different contexts
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2]))

		// Updating the priorities throws an error
		await assertThrowsError(ofType: StatisticsWidget.ContextError.self) {
			try await withDependencies {
				$0[DatabaseService.self].reader = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await self.statisticsWidgets.updatePriorities([UUID(0), UUID(1)])
			}
		}
	}

	// MARK: Create

	func testCreate_WhenWidgetExists_ThrowsError() async throws {
		// Given a database with an existing widget
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), priority: 1)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1]))

		// Create the widget
		await assertThrowsError(ofType: DatabaseError.self) {
			let create = StatisticsWidget.Create(id: UUID(0), created: Date(), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average Pins Left on Deck", context: "", priority: 0)
			try await withDependencies {
				$0[DatabaseService.self].writer = { @Sendable in db }
				$0[StatisticsWidgetsRepository.self] = .liveValue
			} operation: {
				try await self.statisticsWidgets.create(create)
			}
		}

		// Does not insert any records
		let count = try await db.read { try StatisticsWidget.Database.fetchCount($0) }
		XCTAssertEqual(count, 1)

		// Does not update the database
		let updated = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.timeline, .past3Months)
		XCTAssertEqual(updated?.statistic, "Average")
	}

	func testCreate_WhenWidgetNotExists_CreatesWidget() async throws {
		// Given a database with no widgets
		let db = try initializeDatabase(withBowlers: .default, withStatisticsWidgets: nil)

		// Creating a widget
		let create = StatisticsWidget.Create(id: UUID(0), created: Date(), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average Pins Left on Deck", context: "", priority: 0)
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.create(create)
		}

		// Inserted the record
		let exists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)

		// Updates the database
		let updated = try await db.read { try StatisticsWidget.Database.fetchOne($0, id: UUID(0)) }
		XCTAssertEqual(updated?.id, UUID(0))
		XCTAssertEqual(updated?.timeline, .allTime)
		XCTAssertEqual(updated?.statistic, "Average Pins Left on Deck")
	}

	// MARK: Delete

	func testDelete_WhenIdExists_DeletesWidget() async throws {
		// Given a database with 2 bowlers
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
		let widget2 = StatisticsWidget.Database.mock(id: UUID(1), created: Date(timeIntervalSince1970: 2), context: "other", priority: 2)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1, widget2]))

		// Deleting the first bowler
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.delete(UUID(0))
		}

		// Updates the database
		let deletedExists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
		XCTAssertFalse(deletedExists)

		// And leaves the other widget intact
		let otherExists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(1)) }
		XCTAssertTrue(otherExists)
	}

	func testDelete_WhenIdNotExists_DoesNothing() async throws {
		// Given a database with 1 widget
		let widget1 = StatisticsWidget.Database.mock(id: UUID(0), created: Date(timeIntervalSince1970: 1), context: "context", priority: 1)
		let db = try initializeDatabase(withStatisticsWidgets: .custom([widget1]))

		// Deleting a non-existent widget
		try await withDependencies {
			$0[DatabaseService.self].writer = { @Sendable in db }
			$0[StatisticsWidgetsRepository.self] = .liveValue
		} operation: {
			try await self.statisticsWidgets.delete(UUID(1))
		}

		// Leaves the widget
		let exists = try await db.read { try StatisticsWidget.Database.exists($0, id: UUID(0)) }
		XCTAssertTrue(exists)
	}
}
