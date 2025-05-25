import ComposableArchitecture
import StringsLibrary

extension ResourceList {
	func fetchResources(query: Q) -> AsyncThrowingStream<[SectionResourceList<R, Q>.Section], Swift.Error> {
		self.fetchResources(query)
			.map { $0.isEmpty ? [] : [.init(id: "", items: .init(uniqueElements: $0))] }
			.eraseToThrowingStream()
	}
}

extension SectionResourceList {
	public enum AlertAction: Equatable {
		case didTapArchiveButton(R)
		case didTapDeleteButton(R)
		case didTapDismissButton
	}
}

extension SectionResourceList {
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
