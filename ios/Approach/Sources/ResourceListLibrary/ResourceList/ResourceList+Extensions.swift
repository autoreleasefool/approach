import ComposableArchitecture
import StringsLibrary

extension ResourceList {
	func fetchResources(query: Q) -> AsyncThrowingStream<[SectionResourceList<R, Q>.Section], Swift.Error> {
		return .init { continuation in
			let task = Task {
				do {
					for try await resources in self.fetchResources(query) {
						continuation.yield([.init(id: "", items: .init(uniqueElements: resources))])
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}

			continuation.onTermination = { _ in task.cancel() }
		}
	}
}

extension ResourceList.State {
	public mutating func updateQuery(to query: Q) -> Effect<ResourceList.Action> {
		self.sectionList.updateQuery(to: query)
			.map { .internal(.sectionList($0)) }
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
