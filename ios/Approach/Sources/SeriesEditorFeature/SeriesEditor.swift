import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
import FeatureActionLibrary
import FormFeature
import Foundation
import LanesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StringsLibrary

public typealias SeriesForm = Form<Series.Create, Series.Edit>

public struct SeriesEditor: Reducer {
	public struct State: Equatable {
		public let league: League.SeriesHost

		@BindingState public var numberOfGames: Int
		@BindingState public var date: Date
		@BindingState public var preBowl: Series.PreBowl
		@BindingState public var excludeFromStatistics: Series.ExcludeFromStatistics
		@BindingState public var coordinate: CoordinateRegion
		public var location: Alley.Summary?

		public let initialValue: SeriesForm.Value
		public var _form: SeriesForm.State

		@PresentationState public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: InitialValue, inLeague: League.SeriesHost) {
			self.league = inLeague
			switch value {
			case let .create(new):
				self.numberOfGames = new.numberOfGames
				self.date = new.date
				self.preBowl = new.preBowl
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.coordinate = .init(coordinate: .init())
				self.initialValue = .create(new)
			case let .edit(existing):
				self.numberOfGames = existing.numberOfGames
				self.date = existing.date
				self.preBowl = existing.preBowl
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				self.coordinate = .init(coordinate: existing.location?.location?.coordinate.mapCoordinate ?? .init())
				self.initialValue = .edit(existing)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case didTapAlley
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didFinishCreating(Series.Create)
			case didFinishArchiving(Series.Edit)
			case didFinishUpdating(Series.Edit)
		}
		public enum InternalAction: Equatable {
			case form(SeriesForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum InitialValue {
		case create(Series.Create)
		case edit(Series.Edit)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			SeriesForm()
				.dependency(\.records, .init(
					create: series.create,
					update: series.update,
					delete: { _ in },
					archive: series.archive
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none

				case .binding(\.$excludeFromStatistics):
					switch (state.league.excludeFromStatistics, state.preBowl) {
					case (.exclude, _):
						state.excludeFromStatistics = .exclude
					case (_, .preBowl):
						state.excludeFromStatistics = .exclude
					case (.include, .regular):
						break
					}
					return .none

				case .binding(\.$preBowl):
					switch (state.league.excludeFromStatistics, state.preBowl) {
					case (.exclude, _):
						state.excludeFromStatistics = .exclude
					case (_, .preBowl):
						state.excludeFromStatistics = .exclude
					case (.include, .regular):
						state.excludeFromStatistics = .include
					}
					return .none

				case .binding(\.$date):
					state.date = calendar.startOfDay(for: state.date)
					return .none

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleyPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						state.coordinate = .init(coordinate: state.location?.location?.coordinate.mapCoordinate ?? .init())
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didArchive(result):
						return state._form.didFinishArchiving(result)
							.map { .internal(.form($0)) }

					case let .didFinishCreating(series):
						return .concatenate(
							.send(.delegate(.didFinishCreating(series))),
							.run { _ in await dismiss() }
						)

					case let .didFinishArchiving(series):
						return .concatenate(
							.send(.delegate(.didFinishArchiving(series))),
							.run { _ in await dismiss() }
						)

					case let .didFinishUpdating(series):
						return .concatenate(
							.send(.delegate(.didFinishUpdating(series))),
							.run { _ in await dismiss() }
						)

					case .didDiscard, .didDelete, .didFinishDeleting:
						return .run { _ in await dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none

				case .alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: /Action.internal..Action.InternalAction.alleyPicker) {
			ResourcePicker { _ in
				alleys.pickable()
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.Series.Created()
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.Series.Updated()
			case .internal(.form(.delegate(.didFinishArchiving))):
				return Analytics.Series.Archived()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
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
	public static var isSaveableWithoutChanges: Bool { true }

	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
	public var saveButtonText: String { Strings.Action.start }
}

extension Series.Edit: EditableRecord {
	public var isDeleteable: Bool { false }
	public var isArchivable: Bool { true }
	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
}
