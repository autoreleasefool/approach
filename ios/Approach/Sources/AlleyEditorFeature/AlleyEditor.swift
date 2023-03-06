import BaseFormLibrary
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import Foundation
import LaneEditorFeature
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Alley: BaseFormModel {
	static public var modelName = Strings.Alley.title
}

public struct AlleyEditor: ReducerProtocol {
	public typealias Form = BaseForm<Alley, Fields>

	public struct Fields: BaseFormState, Equatable {
		public let alley: Alley?
		public var laneEditor: AlleyLanesEditor.State
		@BindableState public var name = ""
		@BindableState public var address = ""
		@BindableState public var material: Alley.Material = .unknown
		@BindableState public var pinFall: Alley.PinFall = .unknown
		@BindableState public var mechanism: Alley.Mechanism = .unknown
		@BindableState public var pinBase: Alley.PinBase = .unknown

		init(alley: Alley?) {
			self.alley = alley
			self.laneEditor = .init(alley: alley)
		}

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
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
				fields = .init(alley: alley)
				fields.name = alley.name
				fields.address = alley.address ?? ""
				fields.material = alley.material
				fields.pinFall = alley.pinFall
				fields.mechanism = alley.mechanism
				fields.pinBase = alley.pinBase
				self.alleyLanes = .init(alley: alley)
			case .create:
				fields = .init(alley: nil)
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

	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createAlley,
					update: persistenceService.updateAlley,
					delete: persistenceService.deleteAlley
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
						state.alleyLanes.lanes = .init(
							uniqueElements: state.base.form.laneEditor.lanes.map {
								$0.toLane(alley: .placeholder)
							}
						)
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
						return .task { [alleyId = alley.id] in
							let added = lanes.filter { !existingIds.contains($0.id) }.map { $0.toLane(alley: alleyId) }
							let removed = existing.filter { !laneIds.contains($0.id) }
							let updated = lanes.filter { existingIds.contains($0.id) }.map { $0.toLane(alley: alleyId) }

							try await persistenceService.createLanes(added)
							try await persistenceService.updateLanes(updated)
							try await persistenceService.deleteLanes(removed)

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

extension LaneEditor.State {
	func toLane(alley: Alley.ID) -> Lane {
		.init(id: id, label: label, isAgainstWall: isAgainstWall, alley: alley)
	}
}

extension AlleyEditor.Fields {
	public func model(fromExisting existing: Alley?) -> Alley {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			id: alley?.id ?? uuid(),
			name: name,
			address: address.isEmpty ? nil : address,
			material: material,
			pinFall: pinFall,
			mechanism: mechanism,
			pinBase: pinBase
		)
	}
}
