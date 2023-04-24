import AlleysRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import Foundation
import LaneEditorFeature
import LanesRepositoryInterface
import ModelsLibrary
import StringsLibrary

public typealias AlleyForm = Form<Alley.Create, Alley.Edit>

public struct AlleyEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String
		@BindingState public var address: String?
		@BindingState public var material: Alley.Material?
		@BindingState public var pinFall: Alley.PinFall?
		@BindingState public var mechanism: Alley.Mechanism?
		@BindingState public var pinBase: Alley.PinBase?

		public var existingLanes: IdentifiedArrayOf<Lane.Edit>
		public var newLanes: IdentifiedArrayOf<Lane.Create>

		public let initialValue: AlleyForm.Value
		public var _form: AlleyForm.State

		public var _alleyLanes: AlleyLanesEditor.State
		public let hasLanesEnabled: Bool
		public var isLaneEditorPresented = false

		public init(value: InitialValue) {
			let alleyId: Alley.ID
			switch value {
			case let .create(new):
				alleyId = new.id
				self.name = new.name
				self.address = new.address
				self.material = new.material
				self.pinFall = new.pinFall
				self.mechanism = new.mechanism
				self.pinBase = new.pinBase
				self.existingLanes = []
				self.newLanes = []
				self.initialValue = .create(new)
			case let .edit(existing):
				alleyId = existing.alley.id
				self.name = existing.alley.name
				self.address = existing.alley.address
				self.material = existing.alley.material
				self.pinFall = existing.alley.pinFall
				self.mechanism = existing.alley.mechanism
				self.pinBase = existing.alley.pinBase
				self.existingLanes = existing.lanes
				self.newLanes = []
				self.initialValue = .edit(existing.alley)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)
			self._alleyLanes = .init(alley: alleyId, existingLanes: self.existingLanes, newLanes: self.newLanes)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasLanesEnabled = featureFlags.isEnabled(.lanes)
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
			case didCreateLanes(TaskResult<Alley.Create>)
			case didUpdateLanes(TaskResult<Alley.Edit>)
			case form(AlleyForm.Action)
			case alleyLanes(AlleyLanesEditor.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public enum InitialValue {
		case create(Alley.Create)
		case edit(Alley.EditWithLanes)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.lanes) var lanes

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			AlleyForm()
				.dependency(\.records, .init(
					create: alleys.create,
					update: alleys.update,
					delete: alleys.delete
				))
		}

		Scope(state: \.alleyLanes, action: /Action.internal..Action.InternalAction.alleyLanes) {
			AlleyLanesEditor()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setLaneEditor(isPresented):
					state.isLaneEditorPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didCreateLanes(result):
					return state._form.didFinishCreating(result)
						.map { .internal(.form($0)) }

				case let .didUpdateLanes(result):
					return state._form.didFinishUpdating(result)
						.map { .internal(.form($0)) }

				case let .form(.delegate(formAction)):
					switch formAction {
					case let .didCreate(.failure(error)):
						return state._form.didFinishCreating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didUpdate(.failure(error)):
						return state._form.didFinishUpdating(.failure(error))
							.map { .internal(.form($0)) }

					case let .didDelete(.failure(error)):
						return state._form.didFinishDeleting(.failure(error))
							.map { .internal(.form($0)) }

					case let .didCreate(.success(new)):
						return .task { [newLanes = state.newLanes, existingLanes = state.existingLanes] in
							try await lanes.create(Array(newLanes))
							try await lanes.update(Array(existingLanes))
							return .internal(.didCreateLanes(.success(new)))
						}
						catch: { error in
							return .internal(.didCreateLanes(.failure(error)))
						}

					case let .didUpdate(.success(existing)):
						return .task { [newLanes = state.newLanes, existingLanes = state.existingLanes] in
							try await lanes.create(Array(newLanes))
							try await lanes.update(Array(existingLanes))
							return .internal(.didUpdateLanes(.success(existing)))
						} catch: { error in
							return .internal(.didUpdateLanes(.failure(error)))
						}

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .fireAndForget { await self.dismiss() }
					}

				case let .alleyLanes(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .alleyLanes(.view), .alleyLanes(.internal):
					return .none

				case .form(.view), .form(.internal):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension AlleyEditor.State {
	var alleyLanes: AlleyLanesEditor.State {
		get {
			var alleyLanes = _alleyLanes
			alleyLanes.existingLanes = existingLanes
			alleyLanes.newLanes = newLanes
			return alleyLanes
		}
		set {
			_alleyLanes = newValue
			self.existingLanes = newValue.existingLanes
			self.newLanes = newValue.newLanes
		}
	}

	var form: AlleyForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.address = address
				new.material = material
				new.pinFall = pinFall
				new.mechanism = mechanism
				new.pinBase = pinBase
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.address = address
				existing.material = material
				existing.pinFall = pinFall
				existing.mechanism = mechanism
				existing.pinBase = pinBase
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Alley.Create: CreateableRecord {
	public static var modelName = Strings.Alley.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Alley.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
}
