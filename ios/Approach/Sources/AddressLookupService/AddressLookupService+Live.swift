import AddressLookupServiceInterface
import Dependencies
import MapKit

extension AddressLookupService: DependencyKey {
	public static var liveValue: Self = {
		final actor LocalSearch: GlobalActor {
			final class Delegate: NSObject, MKLocalSearchCompleterDelegate {
				@Dependency(\.uuid) var uuid
				var continuation: AsyncThrowingStream<[AddressLookupResult], Error>.Continuation?

				func completerDidUpdateResults(_ completer: MKLocalSearchCompleter) {
					self.continuation?.yield(
						completer.results.map { .init(id: uuid(), completion: $0) }
					)
				}

				func completer(_ completer: MKLocalSearchCompleter, didFailWithError error: Error) {
					self.continuation?.finish(throwing: error)
					self.continuation = nil
				}
			}

			typealias Dependencies = (completer: MKLocalSearchCompleter, delegate: Delegate)

			static let shared = LocalSearch()

			var dependencies: [AnyHashable: Dependencies] = [:]

			func initiate(_ id: AnyHashable) -> AsyncThrowingStream<[AddressLookupResult], Error> {
				let delegate = Delegate()
				let completer = MKLocalSearchCompleter()
				completer.resultTypes = [.address, .pointOfInterest]
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

			func update(_ id: AnyHashable, query: String) {
				Task.detached { @MainActor in
					guard !query.isEmpty else { return }
					guard let (completer, _) = await self.dependencies[id] else { return }
					if completer.isSearching {
						completer.cancel()
					}
					completer.queryFragment = query
				}
			}

			func finish(_ id: AnyHashable) {
				self.dependencies[id]?.delegate.continuation?.finish()
				self.removeDependencies(id: id)
			}

			private func removeDependencies(id: AnyHashable) {
				self.dependencies[id] = nil
			}
		}

		return Self(
			beginSearch: { await LocalSearch.shared.initiate($0) },
			updateSearchQuery: { await LocalSearch.shared.update($0, query: $1) },
			finishSearch: { await LocalSearch.shared.finish($0) },
			lookUpAddress: { result in
				let search = MKLocalSearch(request: .init(completion: result.completion.wrapped))
				let response = try await search.start()
				guard let item = response.mapItems.first else { return nil }
				return .init(
					id: result.id,
					title: result.completion.wrapped.title,
					subtitle: result.completion.wrapped.subtitle,
					coordinate: .init(
						latitude: item.placemark.coordinate.latitude,
						longitude: item.placemark.coordinate.longitude
					)
				)
			}
		)
	}()
}
