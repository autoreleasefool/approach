import AddressLookupServiceInterface
import ConcurrencyExtras
import Dependencies
@preconcurrency import MapKit
import ModelsLibrary

private typealias SearchCache = [AnyHashableSendable: (completer: MKLocalSearchCompleter, delegate: Delegate)]
private typealias ResultCache = [AddressLookupResult.ID: MKLocalSearchCompletion]

extension AddressLookupService: DependencyKey {
	public static var liveValue: Self {

		let searches = LockIsolated<SearchCache>([:])
		let resultCache = LockIsolated<ResultCache>([:])

		return Self(
			beginSearch: { @MainActor id in
				@Dependency(\.uuid) var uuid
				var continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation!
				let stream = AsyncThrowingStream<[AddressLookupResult], Error> {
					continuation = $0
					$0.onTermination = { _ in
						searches.withValue { $0[id] = nil }
					}
				}

				let delegate = Delegate(uuid: uuid, continuation: continuation) { toCache in
					resultCache.withValue { cache in
						cache.merge(toCache) { _, newResult in newResult }
					}
				}
				let completer = MKLocalSearchCompleter()
				completer.resultTypes = [.address, .pointOfInterest]
				completer.delegate = delegate
				searches.withValue { $0[id] = (completer, delegate) }
				return stream
			},
			updateSearchQuery: { @MainActor id, query in
				guard !query.isEmpty else { return }
				guard let (completer, _) = searches.value[id] else { return }
				if completer.isSearching {
					completer.cancel()
				}
				completer.queryFragment = query
			},
			lookUpAddress: { @MainActor result in
				guard let localSearchCompletion = resultCache.value[result.id] else { return nil }
				let search = MKLocalSearch(request: .init(completion: localSearchCompletion))
				let response = try await search.start()
				guard let item = response.mapItems.first else { return nil }
				return Location.Summary(
					id: result.id,
					title: result.title,
					subtitle: result.subtitle,
					coordinate: .init(
						latitude: item.placemark.coordinate.latitude,
						longitude: item.placemark.coordinate.longitude
					)
				)
			}
		)
	}
}

private final class Delegate: NSObject, MKLocalSearchCompleterDelegate, Sendable {
	let uuid: UUIDGenerator
	let continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation
	let cacheResults: @Sendable (ResultCache) -> Void

	init(
		uuid: UUIDGenerator,
		continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation,
		cacheResults: @escaping @Sendable (ResultCache) -> Void
	) {
		self.uuid = uuid
		self.continuation = continuation
		self.cacheResults = cacheResults
	}

	func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
		let lookupResults = completer.results
			.map { (result: AddressLookupResult(id: uuid(), completion: $0), completion: $0) }
		let resultsToCache = lookupResults.reduce(into: ResultCache()) {
			$0[$1.result.id] = $1.completion
		}

		cacheResults(resultsToCache)

		self.continuation.yield(
			lookupResults.map(\.result)
		)
	}

	func completer(_ completer: MKLocalSearchCompleter, didFailWithError error: Error) {
		self.continuation.finish(throwing: error)
	}
}
