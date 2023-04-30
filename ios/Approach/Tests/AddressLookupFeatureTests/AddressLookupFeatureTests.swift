@testable import AddressLookupFeature
import AddressLookupServiceInterface
import ComposableArchitecture
import Foundation
import LocationsRepositoryInterface
import ModelsLibrary
import XCTest

@MainActor
final class AddressLookupFeatureTests: XCTestCase {
	func testView_OnAppear_BeginsSearch() async throws {
		let (results, _) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let beginsSearch = expectation(description: "begins search")
		let store = withDependencies {
			$0.uuid = .incrementing
			$0.addressLookupService.beginSearch = { _ in
				beginsSearch.fulfill()
				return results
			}
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		let task = await store.send(.view(.didAppear))
		await fulfillment(of: [beginsSearch])
		await task.cancel()
	}

	func testView_OnDisappear_FinishesSearch() async throws {
		let (results, _) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let finishesSearch = expectation(description: "finishes search")
		let store = withDependencies {
			$0.uuid = .incrementing
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.finishSearch = { _ in finishesSearch.fulfill() }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))
		await store.send(.view(.didDisappear))

		await fulfillment(of: [finishesSearch])
	}

	func testView_OnCancelButton_DismissesFeature() async throws {
		let dismissed = expectation(description: "dismissed")

		let store = withDependencies {
			$0.dismiss = .init { dismissed.fulfill() }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didTapCancelButton))
		await fulfillment(of: [dismissed])
	}

	func testView_OnResultTapped_LooksUpLocation() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()
		let lookedUpLocation = expectation(description: "looked up location")

		let store = withDependencies {
			$0.uuid = .incrementing
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.finishSearch = { _ in }
			$0.addressLookupService.lookUpAddress = { address in
				XCTAssertEqual(address.id, UUID(0))
				lookedUpLocation.fulfill()
				return nil
			}
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		let expectedResults: [AddressLookupResult] = [.init(id: UUID(0), completion: .init())]
		resultsContinuation.yield(expectedResults)

		await store.receive(.internal(.didReceiveResults(.success(expectedResults)))) {
			$0.results = .init(uniqueElements: expectedResults)
		}

		await store.send(.view(.didTapResult(UUID(0)))) {
			$0.isLoadingAddress = true
		}

		await fulfillment(of: [lookedUpLocation])

		await store.receive(.internal(.didFailToLoadAddress(.init(AddressLookup.LookupError.addressNotFound)))) {
			$0.isLoadingAddress = false
			$0.loadingAddressError = AddressLookup.LookupError.addressNotFound.errorDescription
		}

		await store.send(.view(.didDisappear))
	}

	func testQuery_MatchesInitialQuery() async throws {
		let (results, _) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()
		let initialQuery = "Query"
		let updatedQuery = expectation(description: "updated query")

		let store = withDependencies {
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, query in
				XCTAssertEqual(query, initialQuery)
				updatedQuery.fulfill()
			}
			$0.addressLookupService.finishSearch = { _ in }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: initialQuery),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		await fulfillment(of: [updatedQuery])

		await store.send(.view(.didDisappear))
	}

	func testQuery_OnChange_UpdatesSearch() async throws {
		let (results, _) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()
		let updatedQuery = expectation(description: "updated query")

		let store = withDependencies {
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, query in
				XCTAssertEqual(query, "new query")
				updatedQuery.fulfill()
			}
			$0.addressLookupService.finishSearch = { _ in }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		await store.send(.set(\.$query, "new query")) {
			$0.query = "new query"
		}

		await fulfillment(of: [updatedQuery])

		await store.send(.view(.didDisappear))
	}

	func testQuery_OnChange_ClearsError() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let store = withDependencies {
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, _ in }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		resultsContinuation.finish(throwing: MockResultsError())

		await store.receive(.internal(.didReceiveResults(.failure(MockResultsError())))) {
			$0.loadingResultsError = "results error"
		}

		await store.send(.set(\.$query, "new query")) {
			$0.loadingResultsError = nil
			$0.query = "new query"
		}
	}

	func testResults_OnReceiveNewResults_UpdatesState() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let store = withDependencies {
			$0.uuid = .incrementing
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.finishSearch = { _ in }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		let expectedResults: [AddressLookupResult] = [.init(id: UUID(0), completion: .init())]
		resultsContinuation.yield(expectedResults)

		await store.receive(.internal(.didReceiveResults(.success(expectedResults)))) {
			$0.results = .init(uniqueElements: expectedResults)
		}

		await store.send(.view(.didDisappear))
	}

	func testResults_WhenError_DisplaysError() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let store = withDependencies {
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, _ in }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		resultsContinuation.finish(throwing: MockResultsError())

		await store.receive(.internal(.didReceiveResults(.failure(MockResultsError())))) {
			$0.loadingResultsError = "results error"
		}
	}

	func testLookup_WhenError_DisplaysError() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()

		let store = withDependencies {
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, _ in }
			$0.addressLookupService.finishSearch = { _ in }
			$0.addressLookupService.lookUpAddress = { _ in throw MockLookupError() }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		let expectedResults: [AddressLookupResult] = [.init(id: UUID(0), completion: .init())]
		resultsContinuation.yield(expectedResults)

		await store.receive(.internal(.didReceiveResults(.success(expectedResults)))) {
			$0.results = .init(uniqueElements: expectedResults)
		}

		await store.send(.view(.didTapResult(UUID(0)))) {
			$0.isLoadingAddress = true
		}

		await store.receive(.internal(.didFailToLoadAddress(.init(MockLookupError())))) {
			$0.isLoadingAddress = false
			$0.loadingAddressError = "lookup error"
		}

		await store.send(.view(.didDisappear))
	}

	func testLookup_WhenSuccess_Dismisses() async throws {
		let (results, resultsContinuation) = AsyncThrowingStream<[AddressLookupResult], Error>.streamWithContinuation()
		let location: Location.Summary = .init(
			id: UUID(0),
			title: "title",
			subtitle: "subtitle",
			coordinate: .init(latitude: 123, longitude: 123)
		)
		let editLocation: Location.Edit = .init (
			id: UUID(0),
			title: "title",
			subtitle: "subtitle",
			coordinate: .init(latitude: 123, longitude: 123)
		)

		let dismissed = expectation(description: "dismissed")

		let store = withDependencies {
			$0.dismiss = .init { dismissed.fulfill() }
			$0.addressLookupService.beginSearch = { _ in results }
			$0.addressLookupService.updateSearchQuery = { _, _ in }
			$0.addressLookupService.finishSearch = { _ in }
			$0.addressLookupService.lookUpAddress = { _ in location }
		} operation: {
			TestStore(
				initialState: AddressLookup.State(initialQuery: ""),
				reducer: AddressLookup()
			)
		}

		await store.send(.view(.didAppear))

		let expectedResults: [AddressLookupResult] = [.init(id: UUID(0), completion: .init())]
		resultsContinuation.yield(expectedResults)

		await store.receive(.internal(.didReceiveResults(.success(expectedResults)))) {
			$0.results = .init(uniqueElements: expectedResults)
		}

		await store.send(.view(.didTapResult(UUID(0)))) {
			$0.isLoadingAddress = true
		}

		await store.receive(.delegate(.didSelectAddress(editLocation)))

		await fulfillment(of: [dismissed])
	}
}

private struct MockResultsError: Error, LocalizedError, Equatable {
	public var errorDescription: String? {
		"results error"
	}
}

private struct MockLookupError: Error, LocalizedError, Equatable {
	public var errorDescription: String? {
		"lookup error"
	}
}
