import ComposableArchitecture
import StringsLibrary
import SwiftUI

extension ResourceListSection.State {
	func restartObservation() -> Effect<ResourceListSection.Action> {
		.send(.internal(.observe(query: query)))
	}
}

extension ResourceListSection {
	func beginObservation(query: Q) -> Effect<Action> {
		.run { send in
			for try await resources in fetchResources(query) {
				await send(.internal(.resourcesResponse(.success(resources))))
			}
		} catch: { error, send in
			await send(.internal(.resourcesResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observation, cancelInFlight: true)
	}

	static func alert(toDelete resource: R) -> AlertState<AlertAction> {
		AlertState {
			TextState(Strings.Form.Prompt.delete(resource.name))
		} actions: {
			ButtonState(role: .destructive, action: .didTapDeleteButton(resource)) {
				TextState(Strings.Action.delete)
			}

			ButtonState(role: .cancel, action: .didTapDismissButton) {
				TextState(Strings.Action.cancel)
			}
		}
	}

	static func alert(toArchive resource: R) -> AlertState<AlertAction> {
		AlertState {
			TextState(Strings.Form.Prompt.archive(resource.name))
		} actions: {
			ButtonState(role: .destructive, action: .didTapArchiveButton(resource)) {
				TextState(Strings.Action.archive)
			}

			ButtonState(role: .cancel, action: .didTapDismissButton) {
				TextState(Strings.Action.cancel)
			}
		} message: {
			TextState(Strings.Form.Prompt.Archive.message)
		}
	}
}
