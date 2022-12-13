import AlleysDataProviderInterface
import BaseFormFeature
import ComposableArchitecture
import DateTimeLibrary
import Foundation
import PersistenceServiceInterface
import ResourcePickerFeature
import SharedModelsLibrary
import StringsLibrary

extension Series: BaseFormModel {
	static public var modelName = Strings.Series.title
	public var name: String { date.longFormat }
}

extension Alley: PickableResource {
	static public var pickableModelName = Strings.Alley.title
}

public struct SeriesEditor: ReducerProtocol {
	public typealias Form = BaseForm<Series, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var league: League.ID
		public let hasSetNumberOfGames: Bool
		@BindableState public var numberOfGames: Int
		@BindableState public var date = Date()
		public var alleyPicker: ResourcePicker<Alley>.State

		init(league: League, date: Date) {
			self.league = league.id
			self.hasSetNumberOfGames = league.numberOfGames != nil
			self.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
			self.date = date
			self.alleyPicker = .init(
				selected: Set([league.alley].compactMap { $0 }),
				limit: 1,
				showsCancelHeaderButton: false
			)
		}

		public let isDeleteable = true
		public var isSaveable = true
	}

	public struct State: Equatable {
		public var base: Form.State
		public var initialAlley: Alley?
		public var isAlleyPickerPresented = false
		public let hasAlleysEnabled: Bool
		public let hasLanesEnabled: Bool

		public init(
			league: League,
			mode: Form.Mode,
			date: Date,
			hasAlleysEnabled: Bool,
			hasLanesEnabled: Bool
		) {
			var fields = Fields(league: league, date: date)
			switch mode {
			case let .edit(series):
				fields.date = series.date
				fields.numberOfGames = series.numberOfGames
			case .create:
				break
			}

			self.base = .init(mode: mode, form: fields)
			self.hasAlleysEnabled = hasAlleysEnabled
			self.hasLanesEnabled = hasLanesEnabled
		}
	}

	public enum Action: BindableAction, Equatable {
		case loadInitialData
		case alleyResponse(TaskResult<Alley?>)
		case setAlleyPicker(isPresented: Bool)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case alleyPicker(ResourcePicker<Alley>.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.alleysDataProvider) var alleysDataProvider

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createSeries,
					update: persistenceService.updateSeries,
					delete: persistenceService.deleteSeries
				))
		}

		Scope(state: \.base.form.alleyPicker, action: /Action.alleyPicker) {
			ResourcePicker {
				try await alleysDataProvider.fetchAlleys(.init(filter: [], ordering: .byName))
			}
		}

		Reduce { state, action in
			switch action {
			case .loadInitialData:
				if let alley = state.base.form.alleyPicker.selected.first {
					return .task {
						await .alleyResponse(TaskResult {
							let alleys = try await alleysDataProvider.fetchAlleys(.init(filter: [.id(alley)], ordering: .byName))
							return alleys.first
						})
					}
				}
				return .none

			case let .alleyResponse(.success(alley)):
				state.initialAlley = alley
				return .none

			case .alleyResponse(.failure):
				// TODO: handle error failing to load alley
				return .none

			case let .setAlleyPicker(isPresented):
				state.isAlleyPickerPresented = isPresented
				return .none

			case .alleyPicker(.saveButtonTapped), .alleyPicker(.cancelButtonTapped):
				state.isAlleyPickerPresented = false
				return .none

			case .form(.saveModelResult(.success)):
				return .task { .form(.didFinishSaving) }

			case .form(.deleteModelResult(.success)):
				return .task { .form(.didFinishDeleting) }

			case .binding, .form, .alleyPicker:
				return .none
			}
		}
	}
}

extension SeriesEditor.Fields {
	public func model(fromExisting existing: Series?) -> Series {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			league: league,
			id: existing?.id ?? uuid(),
			date: date,
			numberOfGames: numberOfGames,
			alley: alleyPicker.selected.first
		)
	}
}
