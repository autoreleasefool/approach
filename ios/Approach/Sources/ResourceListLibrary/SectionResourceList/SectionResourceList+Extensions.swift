import ComposableArchitecture

extension SectionResourceList {
	func beginObservation(query: Q) -> Effect<Action> {
		return .run { send in
			for try await sections in fetchSections(query) {
				await send(.internal(.sectionsResponse(.success(sections))))
			}
		} catch: { error, send in
			await send(.internal(.sectionsResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}
}

extension SectionResourceList.State {
	public mutating func updateQuery(to query: Q) -> Effect<SectionResourceList.Action> {
		self.query = query
		return .send(.internal(.refreshObservation))
	}
}
