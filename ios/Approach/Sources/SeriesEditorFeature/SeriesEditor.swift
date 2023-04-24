import AlleysRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import Foundation
import LanesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StringsLibrary

public typealias SeriesForm = Form<Series.Create, Series.Edit>

public struct SeriesEditor: Reducer {
	public struct State: Equatable {
		public let hasAlleysEnabled: Bool
		public let hasLanesEnabled: Bool
		public let league: League.SeriesHost

		@BindingState public var numberOfGames: Int
		@BindingState public var date: Date
		@BindingState public var preBowl: Series.PreBowl
		@BindingState public var excludeFromStatistics: Series.ExcludeFromStatistics
		public var location: Alley.Summary?
		public var lanes: IdentifiedArrayOf<Lane.Summary>

		public let initialValue: SeriesForm.Value
		public var _form: SeriesForm.State

		public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State
		public var isAlleyPickerPresented = false

		public var lanePicker: ResourcePicker<Lane.Summary, Alley.ID?>.State
		public var isLanePickerPresented = false

		public init(value: InitialValue, inLeague: League.SeriesHost) {
			self.league = inLeague
			switch value {
			case let .create(new):
				self.numberOfGames = new.numberOfGames
				self.date = new.date
				self.preBowl = new.preBowl
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.lanes = []
				self.initialValue = .create(new)
			case let .edit(existing):
				self.numberOfGames = existing.series.numberOfGames
				self.date = existing.series.date
				self.preBowl = existing.series.preBowl
				self.excludeFromStatistics = existing.series.excludeFromStatistics
				self.location = existing.series.location
				self.lanes = existing.lanes
				self.initialValue = .edit(existing.series)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)

			self.alleyPicker = .init(
				selected: Set([self.location?.id].compactMap { $0 }),
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)
			self.lanePicker = .init(
				selected: Set(self.lanes.map(\.id)),
				query: self.location?.id,
				showsCancelHeaderButton: false
			)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAlleysEnabled = featureFlags.isEnabled(.alleys)
			self.hasLanesEnabled = featureFlags.isEnabled(.lanes)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case setAlleyPicker(isPresented: Bool)
			case setLanePicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(SeriesForm.Action)
			case alleyPicker(ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID?>.Action)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
		case binding(BindingAction<State>)
	}

	public enum InitialValue {
		case create(Series.Create)
		case edit(Series.EditWithLanes)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.lanes) var lanes
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			SeriesForm()
				.dependency(\.records, .init(
					create: series.create,
					update: series.update,
					delete: series.delete
				))
		}

		Scope(state: \.alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in
				alleys.list(ordered: .byRecentlyUsed)
			}
		}

		Scope(state: \.lanePicker, action: /Action.internal..Action.InternalAction.lanePicker) {
			ResourcePicker { alley in
				lanes.list(alley)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setAlleyPicker(isPresented):
					state.isAlleyPickerPresented = isPresented
					return .none

				case let .setLanePicker(isPresented):
					state.lanes = .init(uniqueElements: state.lanePicker.selectedResources ?? [])
					state.isLanePickerPresented = isPresented
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleyPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isAlleyPickerPresented = false
						state.location = state.alleyPicker.selectedResources?.first
						return state.lanePicker.updateQuery(to: state.location?.id)
							.map { .internal(.lanePicker($0)) }
					}

				case let .lanePicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.lanes = .init(uniqueElements: state.lanePicker.selectedResources ?? [])
						state.isLanePickerPresented = false
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						// TODO: save lanes when series is created
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }


					case let .didUpdate(result):
						// TODO: save lanes when series is edited
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .fireAndForget { await self.dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none

				case .alleyPicker(.internal), .alleyPicker(.view):
					return .none

				case .lanePicker(.internal), .lanePicker(.view):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension SeriesEditor.State {
	var form: SeriesForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.date = date
				new.preBowl = preBowl
				new.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				new.numberOfGames = numberOfGames
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.date = date
				existing.preBowl = preBowl
				existing.excludeFromStatistics = preBowl == .preBowl ? .exclude : excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Series.Create: CreateableRecord {
	public static var modelName = Strings.Series.title

	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
	public var saveButtonText: String { Strings.Action.start }
}

extension Series.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
}

extension Alley.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Alley.title : Strings.Alley.List.title
	}
}

extension Lane.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Lane.title : Strings.Lane.List.title
	}
}
