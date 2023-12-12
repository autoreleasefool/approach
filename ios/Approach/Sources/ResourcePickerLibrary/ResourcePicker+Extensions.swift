import ComposableArchitecture

extension ResourcePicker {
	func beginObservation(query: Query) -> Effect<Action> {
		return .run { send in
			for try await resources in observeResources(query) {
				await send(.internal(.didLoadResources(.success(resources))))
			}
		} catch: { error, send in
			await send(.internal(.didLoadResources(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}
}

extension ResourcePicker.State {
	public mutating func updateQuery(to query: Query) -> Effect<ResourcePicker.Action> {
		self.query = query
		return .send(.internal(.refreshObservation))
	}
}
