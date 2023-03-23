import AddressLookupServiceInterface
import Dependencies
import MapKit

extension AddressLookupService: DependencyKey {
	public static var liveValue: Self = {
		return Self(
			beginSearch: { await LocalSearchActor.shared.initiate($0) },
			updateSearchQuery: { await LocalSearchActor.shared.update($0, query: $1) },
			finishSearch: { await LocalSearchActor.shared.finish($0) },
			lookUpAddress: { result in
				let search = MKLocalSearch(request: .init(completion: result.completion))
				let response = try await search.start()
				guard let item = response.mapItems.first else { return nil }
				return .init(
					id: result.id,
					title: result.completion.title,
					subtitle: result.completion.subtitle,
					latitude: item.placemark.coordinate.latitude,
					longitude: item.placemark.coordinate.longitude
				)
			}
		)
	}()
}

final actor LocalSearchActor: GlobalActor {
	final class Delegate: NSObject, MKLocalSearchCompleterDelegate {
		var continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation?

		func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
			self.continuation?.yield(completer.results.map { .init(completion: $0) })
		}

		func completer(_ completer: MKLocalSearchCompleter, didFailWithError error: Error) {
			self.continuation?.finish(throwing: error)
			self.continuation = nil
		}
	}

	typealias Dependencies = (completer: MKLocalSearchCompleter, delegate: Delegate)

	static let shared = LocalSearchActor()

	var dependencies: [ObjectIdentifier: Dependencies] = [:]

	func initiate(_ id: Any.Type) -> AsyncThrowingStream<[AddressLookupResult], Error> {
		let id = ObjectIdentifier(id)
		let delegate = Delegate()
		let completer = MKLocalSearchCompleter()
		completer.resultTypes = [.query]
		completer.delegate = delegate
		var continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation!
		let stream = AsyncThrowingStream<[AddressLookupResult], Error> {
			$0.onTermination = { _ in
				Task { await self.removeDependencies(id: id) }
			}
			continuation = $0
		}

		delegate.continuation = continuation
		self.dependencies[id] = (completer, delegate)
		return stream
	}

	func update(_ id: Any.Type, query: String) {
		guard !query.isEmpty else { return }
		let id = ObjectIdentifier(id)
		guard let (completer, _) = self.dependencies[id] else { return }
		completer.queryFragment = query
	}

	func finish(_ id: Any.Type) {
		let id = ObjectIdentifier(id)
		self.dependencies[id]?.delegate.continuation?.finish()
		self.removeDependencies(id: id)
	}

	private func removeDependencies(id: ObjectIdentifier) {
		self.dependencies[id] = nil
	}
}
