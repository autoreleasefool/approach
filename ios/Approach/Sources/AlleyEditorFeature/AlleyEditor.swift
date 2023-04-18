import AlleysRepositoryInterface
import BaseFormLibrary
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import Foundation
import LaneEditorFeature
import ModelsLibrary
import StringsLibrary

extension Alley.Editable: BaseFormModel {
	static public var modelName = Strings.Alley.title
}

public struct AlleyEditor: Reducer {
	public typealias Form = BaseForm<Alley.Editable, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindingState public var alley: Alley.Editable
		public var laneEditor: AlleyLanesEditor.State

		public var model: Alley.Editable { alley }
		public let isDeleteable = true
		public var isSaveable: Bool {
			!alley.name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var isLaneEditorPresented = false
		public var alleyLanes: AlleyLanes.State
		public let hasLanesEnabled: Bool

		public init(mode: Form.Mode, hasLanesEnabled: Bool) {
			var fields: Fields
			switch mode {
			case let .edit(alley):
				fields = .init(
					alley: alley,
					// TODO: need to pass actual alley to editor
					laneEditor: .init(alley: nil)
				)
				// TODO: need to pass actual alley to editor
				self.alleyLanes = .init(alley: nil)
			case .create:
				@Dependency(\.uuid) var uuid: UUIDGenerator
				fields = .init(
					alley: .init(
						id: uuid(),
						name: "",
						address: "",
						material: nil,
						pinFall: nil,
						mechanism: nil,
						pinBase: nil
					),
					// TODO: do we need to pass actual alley to editor? probably not
					laneEditor: .init(alley: nil)
				)
				self.alleyLanes = .init(alley: nil)
			}

			self.hasLanesEnabled = hasLanesEnabled
			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case setLaneEditor(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(Form.Action)
			case alleyLanes(AlleyLanes.Action)
			case laneEditor(AlleyLanesEditor.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.alleys) var alleys

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					save: alleys.save,
					delete: { try await alleys.delete($0.id) }
				))
		}

		Scope(state: \.alleyLanes, action: /Action.internal..Action.InternalAction.alleyLanes) {
			AlleyLanes()
		}

		Scope(state: \.base.form.laneEditor, action: /Action.internal..Action.InternalAction.laneEditor) {
			AlleyLanesEditor()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setLaneEditor(isPresented):
					state.isLaneEditorPresented = isPresented
					if !isPresented {
						// TODO: sort lanes
						// TODO: AlleysRepository, support lane mapping
//						state.alleyLanes.lanes = .init(
//							uniqueElements: state.base.form.laneEditor.lanes.map {
//								$0.toLane(alley: .placeholder)
//							}
//						)
					}
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(formAction)):
					switch formAction {
					case let .didSaveModel(alley):
						let existing = state.base.form.laneEditor.existingLanes
						let existingIds = existing.map { $0.id }
						let lanes = state.base.form.laneEditor.lanes
						let laneIds = lanes.map { $0.id }
						return .task {
							// TODO: AlleysRepository, support lane mapping
//							let added = lanes.filter { !existingIds.contains($0.id) }.map { $0.toLane(alley: alleyId) }
//							let removed = existing.filter { !laneIds.contains($0.id) }
//							let updated = lanes.filter { existingIds.contains($0.id) }.map { $0.toLane(alley: alleyId) }
//
//							try await persistenceService.saveLanes(added + updated)
//							try await persistenceService.deleteLanes(removed)

							return .internal(.form(.callback(.didFinishSaving(.success(alley)))))
						} catch: { error in
							return .internal(.form(.callback(.didFinishSaving(.failure(error)))))
						}

					case let .didDeleteModel(alley):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(alley))))) }

					case .didFinishSaving, .didFinishDeleting, .didDiscard:
						return .task { .delegate(.didFinishEditing) }
					}

				case let .laneEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .alleyLanes(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .laneEditor(.internal), .laneEditor(.view):
					return .none

				case .form(.view), .form(.internal), .form(.callback):
					return .none

				case .alleyLanes(.view), .alleyLanes(.internal):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

// TODO: re-enable adding lanes to alleys
// extension LaneEditor.State {
//	func toLane(alley: Alley.ID) -> Lane {
//		.init(id: id, label: label, isAgainstWall: isAgainstWall, alley: alley)
//	}
// }
